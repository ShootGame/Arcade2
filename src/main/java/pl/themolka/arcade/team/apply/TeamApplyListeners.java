package pl.themolka.arcade.team.apply;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.MatchApplyContext;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.team.TeamsGame;

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

    @Handler(priority = Priority.LOWEST)
    public void matchStart(MatchStartedEvent event) {
        for (Team team : this.game.getTeams()) {
            if (this.shouldApplyTo(team)) {
                team.getApplyContext().applyAll(null, MatchApplyContext.EventType.MATCH_START);
            }
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void playerPlay(PlayerJoinedTeamEvent event) {
        GamePlayer player = event.getGamePlayer();
        Team team = event.getTeam();

        if (this.shouldApplyTo(team)) {
            this.game.getServer().getScheduler().runTaskLater(this.game.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (player != null && team != null && player.isOnline()) {
                        team.getApplyContext().applyAll(player, MatchApplyContext.EventType.PLAYER_PLAY);
                    }
                }
            }, 0L);
        }
    }

    // This is a special listener where we SHOULD to manually set
    // player's respawn location by looping all apply contents.
    @Handler(priority = Priority.LOWEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        GamePlayer player = event.getGamePlayer();
        Team team = this.game.getTeam(player);

        if (this.shouldApplyTo(team)) {
            // TODO: This is the worst solution to apply things...
            this.game.getServer().getScheduler().runTaskLater(this.game.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (player != null && team != null && player.isOnline()) {
                        team.getApplyContext().applyAll(player, MatchApplyContext.EventType.PLAYER_RESPAWN);
                    }
                }
            }, 0L);
        }
    }
}
