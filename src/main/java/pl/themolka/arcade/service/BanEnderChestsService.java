package pl.themolka.arcade.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * We need to disable ender chests on the whole server due to the plugin logic
 * incompatibility. Our multi-world system doesn't work with the global ender
 * chests. In the future, we could add a fix which could override ender chests
 * to be per-world compatible.
 */
@ServiceId("BanEnderChests")
public class BanEnderChestsService extends Service {
    public static final String ENDER_CHEST_MESSAGE = ChatColor.RED +
            "You may not %s Ender Chests on this server.";

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestCraft(CraftItemEvent event) {
        if (event.getInventory().getResult() != null && event.getInventory()
                .getResult().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getActor().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "craft"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getActor().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "open"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "place"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestUse(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "use"));
        }
    }
}
