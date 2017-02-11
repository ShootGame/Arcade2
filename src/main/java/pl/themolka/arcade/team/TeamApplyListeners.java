package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;

/**
 * Spawning players and giving them kits.
 */
public class TeamApplyListeners {
    private final TeamsGame game;

    public TeamApplyListeners(TeamsGame game) {
        this.game = game;
    }

    public boolean shouldApplyTo(Team team) {
        return this.game.getMatch().isRunning() && team != null && team.isParticipating();
    }

    //
    // Contents
    //

    @Handler(priority = Priority.HIGH)
    public void applyContentOnJoin(PlayerJoinedTeamEvent event) {
        if (this.shouldApplyTo(event.getTeam())) {
            event.getTeam().apply(event.getGamePlayer(), TeamApplyEvent.JOIN);
        }
    }

    @Handler(priority = Priority.HIGH)
    public void applyContentOnRespawn(PlayerRespawnEvent event) {
        Team team = this.game.getTeam(event.getGamePlayer());
        if (this.shouldApplyTo(team)) {
            team.apply(event.getGamePlayer(), TeamApplyEvent.RESPAWN);
        }
    }

    @Handler(priority = Priority.HIGH)
    public void applyContentOnStart(MatchStartedEvent event) {
        for (Team team : this.game.getTeams()) {
            if (this.shouldApplyTo(team)) {
                team.applyToAll(TeamApplyEvent.START);
            }
        }
    }
}
