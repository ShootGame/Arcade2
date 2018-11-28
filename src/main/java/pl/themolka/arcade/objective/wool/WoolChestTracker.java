package pl.themolka.arcade.objective.wool;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoolChestTracker implements Listener {
    private final Game game;

    private final Map<Block, ChestImage> chestImages = new HashMap<>();

    public WoolChestTracker(Game game) {
        this.game = game;
    }

    @Handler(priority = Priority.LOWEST)
    public void dontBreakWoolChests(BlockTransformEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Block block = event.getBlock();
        if (!block.getType().equals(Material.CHEST)) {
            return;
        }

        if (block.getState() instanceof InventoryHolder) {
            // Register the wool chest is we haven't seen it yet.
            this.registerWoolChest((InventoryHolder) block.getState());
        }

        ChestImage image = this.getChestImage(block);
        if (image != null && image.woolChest) {
            event.setCanceled(true);

            GamePlayer player = event.getGamePlayer();
            if (player != null) {
                player.sendError("You may not break wool chests.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void dontProtectPlacedChests(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof InventoryHolder) {
            this.chestImages.put(block, new ChestImage(false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void registerWoolChests(InventoryMoveItemEvent event) {
        this.registerWoolChest(event.getSource().getHolder());
        this.registerWoolChest(event.getDestination().getHolder());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void registerWoolChests(InventoryOpenEvent event) {
        this.registerWoolChest(event.getInventory().getHolder());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void restoreWoolChests(InventoryCloseEvent event) {
        HumanEntity human = event.getPlayer();
        if (human instanceof Player) {
            GamePlayer player = this.game.resolve((Player) human);
            if (player == null) {
                return;
            }

            List<GamePlayer> viewers = new ArrayList<>();
            for (HumanEntity humanViewer : event.getViewers()) {
                if (humanViewer instanceof Player) {
                    GamePlayer viewer = this.game.resolve((Player) humanViewer);

                    if (viewer != null && viewer.isParticipating()) {
                        viewers.add(viewer);
                    }
                }
            }

            this.restoreWoolChest(event.getInventory().getHolder(), player, viewers);
        }
    }

    private ChestImage getChestImage(Block block) {
        return this.chestImages.get(block);
    }

    private void registerWoolChest(InventoryHolder inventoryHolder) {
        if (inventoryHolder instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) inventoryHolder;

            // register both blocks
            this.registerWoolChest(doubleChest.getLeftSide());
            this.registerWoolChest(doubleChest.getRightSide());
            return;
        } else if (!(inventoryHolder instanceof BlockState)) {
            return;
        }

        Inventory inventory = inventoryHolder.getInventory();
        Block block = ((BlockState) inventoryHolder).getBlock();

        ChestImage image = this.getChestImage(block);
        if (image != null) {
            // We have already seen this chest, don't register it.
            return;
        }

        this.chestImages.put(block, image = new ChestImage(WoolUtils.containsAny(inventory)));

        if (image.woolChest) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && WoolUtils.isWool(item)) {
                    image.snapshot.put(slot, item.clone());
                }
            }
        }
    }

    private void restoreWoolChest(InventoryHolder inventoryHolder, GamePlayer player, List<GamePlayer> viewers) {
        if (inventoryHolder == null) {
            return;
        }

        Inventory inventory = inventoryHolder.getInventory();

        if (!player.isParticipating()) {
            return;
        } else if (inventoryHolder instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) inventoryHolder;

            // restore both blocks
            this.restoreWoolChest(doubleChest.getLeftSide(), player, viewers);
            this.restoreWoolChest(doubleChest.getRightSide(), player, viewers);
            return;
        } else if (!(inventoryHolder instanceof BlockState)) {
            return;
        }

        Block block = ((BlockState) inventoryHolder).getBlock();

        ChestImage image = this.getChestImage(block);
        if (image == null || !image.woolChest) {
            return;
        }

        boolean restoreWools = true;

        // Don't retrace wools if there are players viewing this chest.
        for (GamePlayer viewer : viewers) {
            if (viewer.isParticipating() && !viewer.equals(player)) {
                restoreWools = false;
            }
        }

        if (restoreWools) {
            inventory.clear();
            for (Map.Entry<Integer, ItemStack> entry : image.snapshot.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue().clone());
            }
        }
    }

    private class ChestImage {
        final boolean woolChest;
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        ChestImage(boolean woolChest) {
            this.woolChest = woolChest;
        }
    }
}
