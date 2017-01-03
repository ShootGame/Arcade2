package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalCreateEvent;
import pl.themolka.arcade.goal.GoalScoreEvent;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.session.ArcadePlayerJoinEvent;
import pl.themolka.arcade.session.ArcadePlayerQuitEvent;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandPermissionException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsGame extends GameModule implements Match.IObserverHandler {
    private Match match;
    private Observers observers;
    private final Map<String, Team> teamsById = new HashMap<>();
    private final Map<GamePlayer, Team> teamsByPlayer = new HashMap<>();

    public TeamsGame(Observers observers, List<Team> teams) {
        this.observers = observers;

        this.teamsById.put(observers.getId(), observers);
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
        }

        this.getMatch().setObserverHandler(this);

        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_OBSERVERS, this.getObservers());
        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAMS, this.getTeams().toArray(new Team[this.getTeams().size()]));

        GameModule kitsGame = this.getGame().getModule(KitsModule.class);
        if (kitsGame != null) {
            this.registerListenerObject(new TeamsKitListeners(this, (KitsGame) kitsGame, this.getMatch()));
        }
    }

    @Override
    public boolean isPlayerObserving(GamePlayer player) {
        Team team = this.getTeam(player);
        return team != null && team.isObserving();
    }

    public void autoJoinTeam(GamePlayer player) throws CommandException {
        Team smallestTeam = null;
        for (Team team : this.getTeams()) {
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

        if (smallestTeam == null) {
            throw new CommandException("No teams were found!");
        }

        Team join = smallestTeam;
        if (!player.getPlayer().getBukkit().hasPermission("arcade.command.join.overfill") && join.isOverfill()) {
            throw new CommandException("Teams are full! " + ChatColor.GOLD + "Only " + ChatColor.BOLD +
                    "VIP" + ChatColor.RESET + ChatColor.GOLD + "s can join full teams.");
        } else if (join.isFull()) {
            throw new CommandException("Teams are overfilled!");
        }

        this.joinTeam(player, join);
    }

    public Match getMatch() {
        return this.match;
    }

    public Observers getObservers() {
        return this.observers;
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
        } else if (!player.getPlayer().getBukkit().hasPermission("arcade.command.join.select")) {
            throw new CommandPermissionException("arcade.command.join.select");
        } else if (query == null) {
            throw new CommandException("No query given.");
        }

        Team result = null;
        for (Team team : this.getTeams()) {
            if (team.getName().equalsIgnoreCase(query)) {
                result = team;
                break;
            }
        }

        if (result != null) {
            for (Team team : this.getTeams()) {
                if (team.getName().toLowerCase().contains(query.toLowerCase())) {
                    result = team;
                    break;
                }
            }
        }

        if (result == null) {
            throw new CommandException("No teams found from the given query.");
        } else if (!player.getPlayer().getBukkit().hasPermission("arcade.command.join.overfill") && result.isOverfill()) {
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

    @Handler(priority = Priority.LAST)
    public void onOldTeamRemove(PlayerLeaveTeamEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null && !event.isCanceled()) {
            team.leave(event.getGamePlayer());
        }
    }

    @Handler(priority = Priority.LOWER)
    public void onPlayerJoinGame(GameCommands.JoinCommandEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (event.isAuto()) {
            this.autoJoinTeam(event.getJoinPlayer().getGamePlayer());
        } else {
            this.joinTeam(event.getJoinPlayer().getGamePlayer(), event.getContext().getParams(0));
        }
    }

    @Handler(priority = Priority.HIGH)
    public void onPlayerJoinServer(ArcadePlayerJoinEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());
        this.joinTeam(event.getGamePlayer(), this.getObservers(), false);
    }

    @Handler(priority = Priority.HIGHEST)
    public void onPlayerLeaveGame(GameCommands.LeaveCommandEvent event) {
        this.joinTeam(event.getLeavePlayer().getGamePlayer(), this.getObservers(), true);
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerLeaveServer(ArcadePlayerQuitEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null) {
            team.leaveServer(event.getGamePlayer());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerWantToJoin(GameCommands.JoinCompleterEvent event) {
        for (Team team : this.getTeams()) {
            event.addResult(team.getName().toLowerCase());
        }
    }

    @Handler(priority = Priority.HIGHEST)
    public void onGoalCreate(GoalCreateEvent event) {
        if (event.getGoal() instanceof TeamHolder) {
            Team team = ((TeamHolder) event.getGoal()).getTeam();
            team.addGoal(event.getGoal());
        }
    }

    @Handler(priority = Priority.LAST)
    public void onGoalScore(GoalScoreEvent event) {
        if (!event.isCanceled() && event.getGoal() instanceof TeamHolder) {
            Team team = ((TeamHolder) event.getGoal()).getTeam();
            if (team.areGoalsScored()) {
                this.getMatch().end(team);
            }
        }
    }
}
