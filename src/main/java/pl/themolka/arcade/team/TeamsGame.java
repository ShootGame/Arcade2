package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.jdom2.Element;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandPermissionException;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchStartCountdownEvent;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;
import pl.themolka.arcade.spawn.SpawnsGame;
import pl.themolka.arcade.spawn.SpawnsModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TeamsGame extends GameModule implements Match.IObserverHandler {
    /** Limit the amount of teams in the match */
    public static final int TEAMS_LIMIT = 26;

    /** Commands related to this module */
    private TeamCommands commands;
    /** Match where teams are stored */
    private Match match;
    /** Teams indexed by their unique identifiers */
    private final Map<String, Team> teamsById = new HashMap<>();
    /** Teams indexed by their members */
    private final Map<GamePlayer, Team> teamsByPlayer = new HashMap<>();
    /** Team picker window */
    private TeamWindow window;

    @Override
    public void onEnable() {
        MatchGame matchGame = (MatchGame) this.getGame().getModule(MatchModule.class);
        KitsGame kitsGame = (KitsGame) this.getGame().getModule(KitsModule.class);
        SpawnsGame spawnsGame = (SpawnsGame) this.getGame().getModule(SpawnsModule.class);

        this.commands = new TeamCommands(this);
        this.match = matchGame.getMatch();

        List<Team> teams = new ArrayList<>();
        for (Element teamElement : this.getSettings().getChildren("team")) {
            Team team = XMLTeam.parse(this.getGame().getMap(), teamElement, this.getPlugin(),
                                      kitsGame, spawnsGame);
            if (team != null) {
                team.setBukkit(Team.createBukkitTeam(this.getGame().getScoreboard().getScoreboard(), team));
                teams.add(team);
            }
        }

        for (int i = 0; i < teams.size(); i++) {
            if (i > TEAMS_LIMIT) {
                this.getLogger().log(Level.SEVERE, "There are too many teams (reached limit of " + TEAMS_LIMIT + ")!");
                break;
            }

            Team team = teams.get(i);
            this.teamsById.put(team.getId(), team);
        }

        for (Team team : this.getTeams()) {
            team.setMatch(this.getMatch());
            this.getMatch().registerWinner(team); // register
        }

        this.teamsById.put(this.match.getObservers().getId(), this.match.getObservers());

        this.window = new TeamWindow(this);
        this.getWindow().create();
        this.getGame().getWindowRegistry().addWindow(this.getWindow()); // register
        this.getMatch().setPlayWindow(this.getWindow());

        this.getMatch().setObserverHandler(this);
        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAMS, this.getTeams().toArray(new Team[this.getTeams().size()]));

        // cache
        for (GamePlayer observer : matchGame.getObservers().getOnlineMembers()) {
            this.teamsByPlayer.put(observer, matchGame.getObservers());
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        register.add(new TeamFilters(this));
        register.add(new TeamApplyListeners(this));
        return register;
    }

    @Override
    public boolean isPlayerObserving(GamePlayer player) {
        Team team = this.getTeam(player);
        return team == null || team.isObserving();
    }

    //
    // Getters and Setters
    //

    public boolean areTeamsFull() {
        int did = 0;
        for (Team team : this.getTeams()) {
            if (team.isParticipating()) {
                did++;

                if (!team.isFull()) {
                    return false;
                }
            }
        }

        return did != 0;
    }

    public boolean areTeamsOverfilled() {
        int did = 0;
        for (Team team : this.getTeams()) {
            if (team.isParticipating()) {
                did++;

                if (!team.isOverfilled()) {
                    return false;
                }
            }
        }

        return did != 0;
    }

    public Team findTeamById(String query) {
        if (query.equals(Observers.OBSERVERS_KEY)) {
            return this.getMatch().getObservers();
        }

        Team result = this.getTeam(query);
        if (result != null) {
            return result;
        }

        for (Team team : this.getTeams()) {
            if (team.getId().toLowerCase().startsWith(query.toLowerCase())) {
                return team;
            }
        }

        for (Team team : this.getTeams()) {
            if (team.getName() != null && team.getName().equalsIgnoreCase(query)) {
                return team;
            }
        }

        for (Team team : this.getTeams()) {
            if (team.getName() != null && team.getName().toLowerCase().contains(query.toLowerCase())) {
                return team;
            }
        }

        return null;
    }

    public TeamCommands getCommands() {
        return this.commands;
    }

    public Match getMatch() {
        return this.match;
    }

    public Team getTeam(String id) {
        return this.teamsById.get(id);
    }

    public Team getTeam(GamePlayer player) {
        return this.teamsByPlayer.get(player);
    }

    public Collection<Team> getTeams() {
        return this.teamsById.values();
    }

    public TeamWindow getWindow() {
        return this.window;
    }

    //
    // Team Management
    //

    public void autoJoinTeam(GamePlayer player) throws CommandException {
        Team smallestTeam = null;
        for (Team team : this.getTeams()) {
            if (team.isParticipating()) {
                if (smallestTeam != null) {
                    double first = smallestTeam.getOnlineMembers().size() / smallestTeam.getMaxPlayers();
                    double second = team.getOnlineMembers().size() / team.getMaxPlayers();

                    if (first > second) {
                        smallestTeam = team;
                    }
                } else {
                    smallestTeam = team;
                }
            }
        }

        if (smallestTeam == null) {
            throw new CommandException("No teams were found!");
        }

        Team join = smallestTeam;
        if (!player.hasPermission("arcade.command.join.overfill") && join.isFull()) {
            throw new CommandException("Teams are full! " + ChatColor.GOLD + "Only " + ChatColor.BOLD +
                    "VIP" + ChatColor.RESET + ChatColor.GOLD + "s can join full teams.");
        } else if (this.getTeam(player) != null && this.getTeam(player).equals(join)) {
            throw new CommandException("You already joined " + join.getName() + ".");
        } else if (join.isOverfilled()) {
            throw new CommandException("Teams are overfilled!");
        }

        this.joinTeam(player, join);
    }

    public void joinTeam(GamePlayer player, String query) throws CommandException {
        if (!player.isOnline()) {
            throw new CommandException("Player is not not online.");
        } else if (!player.hasPermission("arcade.command.join.choose")) {
            throw new CommandPermissionException("arcade.command.join.choose");
        } else if (query == null) {
            throw new CommandException("No query given.");
        }

        Team result = null;
        for (Team team : this.getTeams()) {
            if (team.getName() != null && team.getName().equalsIgnoreCase(query)) {
                result = team;
                break;
            }
        }

        if (result == null) {
            for (Team team : this.getTeams()) {
                if (team.getName() != null && team.getName().toLowerCase().contains(query.toLowerCase())) {
                    result = team;
                    break;
                }
            }
        }

        if (result == null) {
            throw new CommandException("No teams found from the given query.");
        } else if (this.getTeam(player) != null && this.getTeam(player).equals(result)) {
            throw new CommandException("You already joined " + result.getPrettyName() + Messageable.ERROR_COLOR + ".");
        } else if (!player.hasPermission("arcade.command.join.overfill") && result.isFull()) {
            throw new CommandException(result.getPrettyName() + Messageable.ERROR_COLOR + " is full! " + ChatColor.GOLD +
                    "Only " + ChatColor.BOLD + "VIP" + ChatColor.RESET + Messageable.ERROR_COLOR + "s can join full teams.");
        } else if (result.isOverfilled()) {
            throw new CommandException("Teams are overfilled!");
        } else {
            this.joinTeam(player, result);
        }
    }

    public void joinTeam(GamePlayer player, Team team) {
        this.joinTeam(player, team, true);
    }

    public void joinTeam(GamePlayer player, Team team, boolean message) {
        Team oldTeam = this.getTeam(player);
        if (oldTeam != null) {
            oldTeam.leave(player);
        }

        team.join(player, message);
    }

    //
    // Listeners
    //

    @Handler(priority = Priority.HIGHEST)
    public void onChannelsUpdate(MatchStartedEvent event) {
        for (Team team : this.getTeams()) {
            for (GamePlayer player : team.getOnlineMembers()) {
                player.setCurrentChannel(team.getChannel());
                player.setParticipating(team.isParticipating());
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void onNewTeamPut(PlayerJoinTeamEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (!event.isCanceled() && this.teamsByPlayer.containsKey(player)) {
            this.getTeam(player).leave(player);
            this.teamsByPlayer.remove(player);
        }

        this.teamsByPlayer.put(player, event.getTeam());
    }

    @Handler(priority = Priority.LAST)
    public void onMatchEmpty(PlayerLeftTeamEvent event) {
        Team team = event.getTeam();

        int min = team.getMinPlayers();
        if (min != 0 && !team.getMatch().isForceStart() && team.isParticipating() && team.getOnlineMembers().size() < min) {
            team.getMatch().matchEmpty(team);
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onMatchStartCountdown(MatchStartCountdownEvent event) {
        if (!this.getMatch().isStarting() ||
                this.getPlugin().getGames().getCycleCountdown().isTaskRunning() ||
                this.getPlugin().getGames().getRestartCountdown().isTaskRunning()) {
            event.setCanceled(true);
            return;
        }

        boolean idle = false;
        for (Team team : this.getTeams()) {
            if (team.isParticipating() && team.getOnlineMembers().size() < team.getMinPlayers()) {
                idle = true;
                break;
            }
        }

        if (!this.getMatch().isForceStart() && idle) {
            event.setCanceled(true);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onOldTeamRemove(PlayerLeaveTeamEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null && !event.isCanceled()) {
            this.teamsByPlayer.remove(event.getGamePlayer());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerJoinGame(GameCommands.JoinCommandEvent event) {
        if (event.isCanceled()) {
            return;
        }

        try {
            Team team = this.getTeam(event.getJoinGamePlayer());
            if (team != null && !team.equals(this.getMatch().getObservers()) && this.getMatch().isRunning()) {
                throw new CommandException("You are already in the game.");
            } else if (event.isAuto()) {
                this.autoJoinTeam(event.getJoinGamePlayer());
            } else {
                this.joinTeam(event.getJoinGamePlayer(), event.getContext().getParams(0));
            }

            event.setJoined(true);
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                event.getSender().sendError(ex.getMessage());
            }

            event.setJoined(false);
            event.setCanceled(true);
        }
    }

    @Handler(priority = Priority.HIGH)
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());

        try {
            this.joinTeam(event.getGamePlayer(), this.getMatch().getObservers(), false);
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                event.getPlayer().sendError(ex.getMessage());
            }
        }
    }

    @Handler(priority = Priority.HIGH)
    public void onPlayerLeaveGame(GameCommands.LeaveCommandEvent event) {
        try {
            Team team = this.getTeam(event.getLeaveGamePlayer());
            if (team != null && team.equals(this.getMatch().getObservers())) {
                throw new CommandException("You are not in the game.");
            }

            this.joinTeam(event.getLeaveGamePlayer(), this.getMatch().getObservers(), true);
        } catch (CommandException ex) {
            if (ex.getMessage() != null) {
                event.getSender().sendError(ex.getMessage());
            } else {
                event.getSender().sendError("Could not join observers right now. Please try again later.");
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerLeaveServer(PlayerQuitEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null) {
            team.leaveServer(event.getGamePlayer());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerWantsToJoin(GameCommands.JoinCompleterEvent event) {
        String request = event.getContext().getParams(0);
        if (request == null) {
            request = "";
        }

        for (Team team : this.getTeams()) {
            if (team.getName() != null && team.getName().toLowerCase().startsWith(request.toLowerCase())) {
                event.addResult(team.getName());
            }
        }
    }
}
