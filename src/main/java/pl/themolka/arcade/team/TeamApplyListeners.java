package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.match.MatchApplyContext;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.spawn.SpawnApply;
import pl.themolka.arcade.time.Time;

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
                for (GamePlayer player : team.getOnlineMembers()) {
                    player.getPlayer().clearInventory(true);
                }

                team.getApplyContext().applyAll(null, MatchApplyContext.EventType.MATCH_START);
            }
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void playerPlay(PlayerJoinedTeamEvent event) {
        GamePlayer player = event.getGamePlayer();
        Team team = event.getTeam();

        if (this.shouldApplyTo(team)) {
            player.getPlayer().clearInventory(true);

            team.getApplyContext().applyAll(player, MatchApplyContext.EventType.PLAYER_PLAY);
        }
    }

    // This is a special listener where we manually set player's
    // respawn location by looping all apply contents.
    @Handler(priority = Priority.LOWEST)
    public void playerRespawn(PlayerRespawnEvent event) {
        GamePlayer player = event.getGamePlayer();
        Team team = this.game.getTeam(player);

        if (this.shouldApplyTo(team)) {
            player.getPlayer().clearInventory(true);

            Location spawn = this.fetchSpawn(team, MatchApplyContext.EventType.PLAYER_RESPAWN, player);
            if (spawn != null) {
                event.setRespawnPosition(spawn);
            }

            // The player must be spawned to be able to be meta attachable.
            this.game.getServer().getScheduler().runTaskLater(this.game.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        return;
                    }

                    for (PlayerApplicable content : team.getApplyContext().getAllContent(
                            MatchApplyContext.EventType.PLAYER_RESPAWN)) {
                        if (!(content instanceof SpawnApply)) {
                            content.apply(player);
                        }
                    }
                }
            }, Time.ZERO.toTicks());
        }
    }

    private Location fetchSpawn(Team team, MatchApplyContext.EventType event, GamePlayer player) {
        for (PlayerApplicable content : team.getApplyContext().getAllContent(event)) {
            if (content instanceof SpawnApply) {
                SpawnApply spawnApply = (SpawnApply) content;

                return spawnApply.getAgentCreator().createAgent(spawnApply.getSpawn(),
                                                                player,
                                                                player.getBukkit()).getLocation();
            }
        }

        return null;
    }
}
