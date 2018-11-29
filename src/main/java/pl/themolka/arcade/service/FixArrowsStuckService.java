package pl.themolka.arcade.service;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;

/**
 * There is a bug in Minecraft which doesn't remove arrows from the player when
 * he respawns. We need to fix it manually.
 */
@ServiceId("FixArrowsStuck")
public class FixArrowsStuckService extends Service {
    @Handler(priority = Priority.HIGHEST)
    public void fixArrowsStuck(PlayerRespawnEvent event) {
        event.getBukkitPlayer().setArrowsStuck(0);
    }
}
