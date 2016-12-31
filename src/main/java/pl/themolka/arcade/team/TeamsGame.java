package pl.themolka.arcade.team;

import com.google.common.eventbus.Subscribe;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayerJoinEvent;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandPermissionException;

import java.util.Collection;
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
    }

    public void autoJoinTeam(GamePlayer player) {

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

    public void joinTeam(GamePlayer player, CommandContext context) {
        if (!player.isOnline()) {
            throw new CommandException("Player is not not online.");
        } else if (!player.getPlayer().getBukkit().hasPermission("arcade.command.join.select")) {
            throw new CommandPermissionException("arcade.command.join.select");
        }

        String query = context.getParams(0);
        if (query == null) {
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

        if (result != null) {
            result.join(player);
        } else {
            throw new CommandException("No teams found from the given query.");
        }
    }

    @Subscribe
    public void onNewTeamPut(PlayerJoinTeamEvent event) {
        this.teamsByPlayer.put(event.getGamePlayer(), event.getTeam());
    }

    @Subscribe
    public void onOldTeamRemove(PlayerLeaveTeamEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());
    }

    @Subscribe
    public void onPlayerJoinGame(GameCommands.JoinCommandEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (event.isAuto()) {
            this.autoJoinTeam(event.getJoinPlayer().getGamePlayer());
        } else {
            this.joinTeam(event.getJoinPlayer().getGamePlayer(), event.getContext());
        }
    }

    @Subscribe
    public void onPlayerWantToJoin(GameCommands.JoinCompleterEvent event) {
        for (Team team : this.getTeams()) {
            event.addResult(team.getName().toLowerCase());
        }
    }

    @Subscribe
    public void onPlayerJoinServer(ArcadePlayerJoinEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());

        this.getObservers().join(event.getGamePlayer(), false);
    }
}
