package pl.themolka.arcade.match;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;

public class MatchListeners implements Listener {
    private final MatchGame game;

    public MatchListeners(MatchGame game) {
        this.game = game;
    }

    @EventHandler
    public void onBlock36Damage(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.PISTON_MOVING_PIECE)) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onBlockTransform(BlockTransformEvent event) {
        if (this.isMatchIdle()) {
            event.setCanceled(true);
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
