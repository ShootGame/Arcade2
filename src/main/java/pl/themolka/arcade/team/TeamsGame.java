package pl.themolka.arcade.team;

import com.google.common.eventbus.Subscribe;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;

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

    @Subscribe
    public void onNewTeamPut(PlayerJoinTeamEvent event) {
        this.teamsByPlayer.put(event.getGamePlayer(), event.getTeam());
    }

    @Subscribe
    public void onOldTeamRemove(PlayerLeaveTeamEvent event) {
        this.teamsByPlayer.remove(event.getGamePlayer());
    }
}
