package pl.themolka.arcade.team;

import com.google.common.eventbus.Subscribe;
import org.bukkit.ChatColor;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayerJoinEvent;
import pl.themolka.arcade.session.ArcadePlayerQuitEvent;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandPermissionException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsGame extends GameModule {
    private final ObserversTeam observers;
    private final Map<String, Team> teamsById = new HashMap<>();
    private final Map<GamePlayer, Team> teamsByPlayer = new HashMap<>();

    public TeamsGame(ObserversTeam observers, List<Team> teams) {
        this.observers = observers;

        this.teamsById.put(observers.getId(), observers);
        for (Team team : teams) {
            this.teamsById.put(team.getId(), team);
        }
    }

    @Override
    public void onEnable() {
        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_OBSERVERS, this.getObservers());
        this.getGame().setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAMS, this.getTeams().toArray(new Team[this.getTeams().size()]));
    }

    @Override
    public List<Object> getListenerObjects() {
        return Collections.singletonList(new ObserverListeners(this));
    }

    public void autoJoinTeam(GamePlayer player) throws CommandException{
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

    public ObserversTeam getObservers() {
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

    @Subscribe
    public void onNewTeamPut(PlayerJoinTeamEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (this.teamsByPlayer.containsKey(player)) {
            this.getTeam(player).leave(player);
            this.teamsByPlayer.remove(player);
        }

        this.teamsByPlayer.put(player, event.getTeam());
    }

    @Subscribe
    public void onOldTeamRemove(PlayerLeaveTeamEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null) {
            team.leave(event.getGamePlayer());
        }
    }

    @Subscribe
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

    @Subscribe
    public void onPlayerJoinServer(ArcadePlayerJoinEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());
        this.joinTeam(event.getGamePlayer(), this.getObservers(), false);
    }

    @Subscribe
    public void onPlayerLeaveGame(GameCommands.LeaveCommandEvent event) {
        this.joinTeam(event.getLeavePlayer().getGamePlayer(), this.getObservers());
    }

    @Subscribe
    public void onPlayerLeaveServer(ArcadePlayerQuitEvent event) {
        Team team = this.getTeam(event.getGamePlayer());
        if (team != null) {
            team.leaveServer(event.getGamePlayer());
        }
    }

    @Subscribe
    public void onPlayerWantToJoin(GameCommands.JoinCompleterEvent event) {
        for (Team team : this.getTeams()) {
            event.addResult(team.getName().toLowerCase());
        }
    }
}
