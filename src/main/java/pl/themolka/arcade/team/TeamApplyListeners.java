package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
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

    @Handler(priority = Priority.HIGHEST)
    public void applyContentOnJoin(PlayerJoinedTeamEvent event) {
        if (this.shouldApplyTo(event.getTeam())) {
            event.getTeam().apply(event.getGamePlayer(), TeamApplyEvent.JOIN);
        }
    }

    @Handler(priority = Priority.HIGHEST)
    public void applyContentOnRespawn(PlayerRespawnEvent event) {
        Team team = this.game.getTeam(event.getGamePlayer());
        if (this.shouldApplyTo(team)) {
            team.apply(event.getGamePlayer(), TeamApplyEvent.RESPAWN);
        }
    }

    @Handler(priority = Priority.HIGHEST)
    public void applyContentOnStart(MatchStartedEvent event) {
        for (Team team : this.game.getTeams()) {
            if (this.shouldApplyTo(team)) {
                team.applyToAll(TeamApplyEvent.START);
            }
        }
    }

    //
    // Refreshing
    //

    @Handler(priority = Priority.HIGH)
    public void onMatchStarted(MatchStartedEvent event) {
        for (Team team : this.game.getTeams()) {
            if (team.isParticipating()) {
                for (GamePlayer member : team.getOnlineMembers()) {
                    member.refresh();
                }
            }
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void onPlayerLeftTeam(PlayerLeftTeamEvent event) {
        if (event.getTeam().isParticipating()) {
            event.getGamePlayer().refresh();
        }
    }
}
