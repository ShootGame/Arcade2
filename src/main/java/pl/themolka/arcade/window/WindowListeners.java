package pl.themolka.arcade.window;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

public class WindowListeners implements Listener {
    private final ArcadePlugin plugin;

    public WindowListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
            return;
        }

        Window window = this.fetchWindow(event.getInventory());
        if (window == null) {
            return;
        }

        GamePlayer player = this.fetchPlayer(event.getWhoClicked());
        if (player == null) {
            return;
        }

        boolean click = window.click(player, event.getClick(), event.getSlot());
        if (!click) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Window window = this.fetchWindow(event.getInventory());
        if (window == null) {
            return;
        }

        GamePlayer player = this.fetchPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        boolean close = window.close(player);
        if (!close) {
            // don't use the open(...) method in Window, because
            // it will handle the onOpen(...) event.
            event.getPlayer().openInventory(window.getContainer());
        }
    }

    private GamePlayer fetchPlayer(HumanEntity human) {
        ArcadePlayer player = this.plugin.getPlayer(human);
        if (player != null) {
            return player.getGamePlayer();
        }

        return null;
    }

    private Window fetchWindow(Inventory view) {
        return this.plugin.getWindowRegistry().getWindow(view);
    }
}
