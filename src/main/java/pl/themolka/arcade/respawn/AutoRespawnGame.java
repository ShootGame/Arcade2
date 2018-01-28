package pl.themolka.arcade.respawn;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.time.Time;

public class AutoRespawnGame extends GameModule {
    private final Time cooldown;

    public AutoRespawnGame(Time cooldown) {
        this.cooldown = cooldown;
    }

    public Time getCooldown() {
        return this.cooldown;
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setAutoRespawn(true, this.getCooldown());
    }
}
