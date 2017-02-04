package pl.themolka.arcade.match;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlock36Damage(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.PISTON_MOVING_PIECE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.HIGHEST)
    public void onBlockTransform(BlockTransformEvent event) {
        if (this.isMatchIdle()) {
            event.setCanceled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onThunderChange(ThunderChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    private boolean isMatchIdle() {
        return !this.game.getMatch().isRunning();
    }
}
