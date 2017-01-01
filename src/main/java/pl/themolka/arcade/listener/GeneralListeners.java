package pl.themolka.arcade.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.world.PortalCreateEvent;
import pl.themolka.arcade.ArcadePlugin;

public class GeneralListeners implements Listener {
    private final ArcadePlugin plugin;

    public GeneralListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnderChestCraft(CraftItemEvent event) {
        if (event.getInventory().getResult() != null && event.getInventory().getResult().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderChestPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        event.setCancelled(true);
    }
}
