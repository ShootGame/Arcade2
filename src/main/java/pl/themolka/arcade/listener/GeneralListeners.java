package pl.themolka.arcade.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.PortalCreateEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.ArcadePlayerMoveEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        GamePlayer player = game.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            return;
        }

        ArcadePlayerMoveEvent wrapper = new ArcadePlayerMoveEvent(this.plugin, player, event);
        this.plugin.getEventBus().publish(wrapper);

        if (wrapper.isCanceled()) {
            Location to = event.getFrom().clone();
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);

            event.setTo(to);
        }
    }
}
