package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandPermissionException;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchStartCountdownEvent;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeamsGame extends GameModule implements Match.IObserverHandler {
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

    protected TeamsGame(Game game, IGameConfig.Library library, Config config) {
        for (Team.Config team : config.teams().get()) {
            this.teamsById.put(team.id(), library.getOrDefine(game, team));
        }
    }

    @Override
    public void onEnable() {
        this.commands = new TeamCommands(this);

        MatchGame module = (MatchGame) this.getGame().getModule(MatchModule.class);
        this.match = module.getMatch();

        for (Team team : this.teamsById.values()) {
            team.setMatch(this.match);
            this.match.registerWinner(team);
        }

        this.teamsById.put(this.match.getObservers().getId(), this.match.getObservers());

        this.window = new TeamWindow(this);
        this.window.create();
        this.getGame().getWindowRegistry().addWindow(this.getWindow()); // register
        this.match.setPlayWindow(this.getWindow());

        this.match.setObserverHandler(this);

        // cache
        Observers observers = this.match.getObservers();
        for (GamePlayer observer : observers.getOnlineMembers()) {
            this.teamsByPlayer.put(observer, observers);
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
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

        Collection<Team> teams = this.teamsById.values();
        for (Team team : teams) {
            if (team.getId().toLowerCase().startsWith(query.toLowerCase())) {
                return team;
            }
        }

        for (Team team : teams) {
            String name = team.getName();
            if (name != null && name.equalsIgnoreCase(query)) {
                return team;
            }
        }

        for (Team team : teams) {
            String name = team.getName();
            if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
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
        return new ArrayList<>(this.teamsById.values());
    }

    public TeamWindow getWindow() {
        return this.window;
    }

    //
    // Team Management
    //

    public void autoJoinTeam(GamePlayer player) throws CommandException {
        Team smallestTeam = null;
        for (Team team : this.teamsById.values()) {
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
            throw new CommandException("Player is not online.");
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
        if (!team.isParticipating() || team.getMatch().isForceStart()) {
            return;
        }

        int min = team.getMinPlayers();
        if (!this.match.isForceStart() && min != 0 && team.getOnlineMembers().size() < min) {
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
            if (team == null || team.equals(this.getMatch().getObservers())) {
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

    public interface Config extends IGameModuleConfig<TeamsGame> {
        int TEAMS_LIMIT = 26;

        Ref<Set<Team.Config>> teams();

        @Override
        default TeamsGame create(Game game, Library library) {
            return new TeamsGame(game, library, this);
        }
    }
}
