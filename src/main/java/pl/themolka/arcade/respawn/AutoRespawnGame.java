package pl.themolka.arcade.respawn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

public class AutoRespawnGame extends GameModule {
    private final long cooldown;

    public AutoRespawnGame(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void respawn(GamePlayer player) {
        ArcadePlayer source = player.getPlayer();
        if (source != null && player.isParticipating()) {
            source.respawn();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GamePlayer player = this.getGame().getPlayer(event.getEntity());
        if (player != null && player.isParticipating()) {
            this.getServer().getScheduler().runTaskLater(this.getPlugin(), () ->
                    this.respawn(player), this.getCooldown());
        }
    }
}
