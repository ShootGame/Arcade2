package pl.themolka.arcade.match;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MatchListeners implements Listener {
    private final MatchGame game;

    public MatchListeners(MatchGame game) {
        this.game = game;
    }

    @EventHandler
    public void onArrowsRemove(PlayerRespawnEvent event) {
        event.getPlayer().setArrowsStuck(0);
    }

    @EventHandler
    public void onBlock36Damage(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.PISTON_MOVING_PIECE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    private boolean isMatchIdle() {
        return !this.game.getMatch().getState().equals(MatchState.RUNNING);
    }
}
