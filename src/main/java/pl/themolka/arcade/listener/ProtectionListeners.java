package pl.themolka.arcade.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.arcade.ArcadePlugin;

import java.util.ArrayList;

public class ProtectionListeners implements Listener {
    private final ArcadePlugin plugin;

    public ProtectionListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void antiLogout(PlayerQuitEvent event) {
        PlayerDeathEvent fakeEvent = new PlayerDeathEvent(event.getPlayer(), new ArrayList<>(), 0, null);
        this.plugin.getServer().getPluginManager().callEvent(fakeEvent);
    }

    @EventHandler
    public void safeWorkbenches(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasBlock()) {
            Block block = event.getClickedBlock();
            if (block.getType().equals(Material.WORKBENCH)) {
                event.setCancelled(true);
                event.getPlayer().openWorkbench(null, true);
            }
        }
    }
}
