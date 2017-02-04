package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandPermissionException;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchStartCountdownEvent;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsGame extends GameModule implements Match.IObserverHandler {
    private Match match;
    private final Map<String, Team> teamsById = new HashMap<>();
    private final Map<GamePlayer, Team> teamsByPlayer = new HashMap<>();

    public TeamsGame(List<Team> teams) {
        for (Team team : teams) {
            this.teamsById.put(team.getId(), team);
        }
    }

    @Override
    public void onEnable() {
        MatchGame matchGame = (MatchGame) this.getGame().getModule(MatchModule.class);
        this.match = matchGame.getMatch();

        for (Team team : this.getTeams()) {
            team.setMatch(this.getMatch());
            this.getMatch().registerWinner(team);
        }

        this.teamsById.put(this.match.getObservers().getId(), this.match.getObservers());

        this.getMatch().setObserverHandler(this);
        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAMS, this.getTeams().toArray(new Team[this.getTeams().size()]));

        // cache
        for (GamePlayer observer : matchGame.getObservers().getOnlineMembers()) {
            this.teamsByPlayer.put(observer, matchGame.getObservers());
        }

        GameModule kitsGame = this.getGame().getModule(KitsModule.class);
        if (kitsGame != null) {
            this.registerListenerObject(new TeamKitListeners(this, (KitsGame) kitsGame, this.getMatch()));
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        register.add(new TeamFilters(this));
        return register;
    }

    @Override
    public boolean isPlayerObserving(GamePlayer player) {
        Team team = this.getTeam(player);
        return team == null || team.isObserving();
    }

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
        if (!player.getBukkit().hasPermission("arcade.command.join.overfill") && join.isOverfill()) {
            throw new CommandException("Teams are full! " + ChatColor.GOLD + "Only " + ChatColor.BOLD +
                    "VIP" + ChatColor.RESET + ChatColor.GOLD + "s can join full teams.");
        } else if (this.getTeam(player) != null && this.getTeam(player).equals(join)) {
            throw new CommandException("You already joined " + join.getName() + ".");
        } else if (join.isFull()) {
            throw new CommandException("Teams are overfilled!");
        }

        this.joinTeam(player, join);
    }

    public Team findTeamById(String query) {
        Team result = this.getTeam(query);
        if (result != null) {
            return result;
        }

        for (Team team : this.getTeams()) {
            if (team.getId().toLowerCase().startsWith(query.toLowerCase())) {
                return team;
            }
        }

        return null;
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

    public void joinTeam(GamePlayer player, String query) throws CommandException {
        if (!player.isOnline()) {
            throw new CommandException("Player is not not online.");
        } else if (!player.getBukkit().hasPermission("arcade.command.join.choose")) {
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
            throw new CommandException("You already joined " + result.getName() + ".");
        } else if (!player.getBukkit().hasPermission("arcade.command.join.overfill") && result.isOverfill()) {
            throw new CommandException("Teams are full! " + ChatColor.GOLD + "Only " + ChatColor.BOLD +
                    "VIP" + ChatColor.RESET + ChatColor.GOLD + "s can join full teams.");
        } else if (result.isFull()) {
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

    @Handler(priority = Priority.LAST)
    public void onNewTeamPut(PlayerJoinTeamEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (!event.isCanceled() && this.teamsByPlayer.containsKey(player)) {
            this.getTeam(player).leave(player);
            this.teamsByPlayer.remove(player);
        }

        this.teamsByPlayer.put(player, event.getTeam());
    }

    @Handler(priority = Priority.HIGHER)
    public void onMatchStartCountdown(MatchStartCountdownEvent event) {
        if (!this.getMatch().isStarting()) {
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

        if (idle) {
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

    //
    // Commands
    //

    public void clearCommand(Sender sender, String teamId) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot clear observers.");
        }

        int result = 0;
        for (GamePlayer player : team.getOnlineMembers()) {
            team.leaveForce(player);
            result++;

            this.getMatch().getObservers().joinForce(player);
        }

        sender.sendSuccess(team.getName() + " has been cleared (" + result + " players).");
    }

    public void forceCommand(Sender sender, String username, String teamId) {
        GamePlayer player = this.fetchPlayer(username);
        Team team = this.fetchTeam(teamId);

        if (team.contains(player)) {
            throw new CommandException(player.getUsername() + " is already member of " + team.getName() + ".");
        }

        team.joinForce(player);
        sender.sendSuccess(player.getUsername() + " has been moved to " + team.getName() + ".");
    }

    public void kickCommand(Sender sender, String username) {
        GamePlayer player = this.fetchPlayer(username);
        Team team = this.getTeam(player);

        if (team.isObservers()) {
            throw new CommandException("Cannot kick from observers.");
        }

        team.leaveForce(player);
        team.getMatch().getObservers().joinForce(player);
        sender.sendSuccess(player.getUsername() + " has been kicked from " + team.getName() + ".");
    }

    public void minCommand(Sender sender, String teamId, int min) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        } else if (min < 0) {
            throw new CommandException("Number cannot be negative.");
        }

        sender.sendSuccess(team.getName() + " has been edited.");
    }

    public void overfillCommand(Sender sender, String teamId, int overfill) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        }

        // set to unlimited if zero or negative
        int max = Integer.MAX_VALUE;
        if (overfill > 0) {
            max = overfill;
        }

        team.setMaxPlayers(max);

        if (max > team.getSlots()) {
            team.setSlots(max); // slots
        }

        sender.sendSuccess(team.getName() + " has been edited.");
    }

    public void renameCommand(Sender sender, String teamId, String name) {
        Team team = this.fetchTeam(teamId);
        if (name == null) {
            throw new CommandException("New name not given.");
        } else if (name.length() > Team.NAME_MAX_LENGTH) {
            throw new CommandException("Name too long (greater than " + Team.NAME_MAX_LENGTH + " characters).");
        } else if (team.getName().equals(name)) {
            throw new CommandException("Already named '" + team.getName() + "'.");
        }

        String oldName = team.getName();
        team.setName(name);

        this.getPlugin().getEventBus().publish(new TeamRenameEvent(this.getPlugin(), oldName, team));
        sender.sendSuccess(team.getName() + " has been edited.");
    }

    public void slotsCommand(Sender sender, String teamId, int slots) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        }

        // set to unlimited if zero or negative
        int max = Integer.MAX_VALUE;
        if (slots > 0) {
            max = slots;
        }

        team.setSlots(max);

        if (max > team.getMaxPlayers()) {
            team.setMaxPlayers(max); // overfill
        }

        sender.sendSuccess(team.getName() + " has been edited.");
    }

    private GamePlayer fetchPlayer(String player) {
        GamePlayer result = this.getGame().findPlayer(player);
        if (result != null) {
            return result;
        }

        throw new CommandException("Player not found.");
    }

    private Team fetchTeam(String team) {
        Team result = this.findTeamById(team);
        if (result != null) {
            return result;
        }

        throw new CommandException("Team not found.");
    }
}
