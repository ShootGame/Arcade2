package pl.themolka.arcade.capture.wool;

import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WoolCapturableListeners implements Listener {
    private final CaptureGame game;

    private final Map<Block, ChestSnapshot> chests = new HashMap<>();
    private final Multimap<GoalHolder, WoolCapturable> wools;

    public WoolCapturableListeners(CaptureGame game, Multimap<GoalHolder, WoolCapturable> wools) {
        this.game = game;

        this.wools = wools;
    }

    //
    // Chest Protection
    //

    public ChestSnapshot getChest(Block block) {
        return this.chests.get(block);
    }

    @Handler(priority = Priority.LOWEST)
    public void dontBreakWoolChests(BlockTransformEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Block block = event.getBlock();

        ChestSnapshot snapshot = this.getChest(block);
        if (snapshot != null && snapshot.woolChest) {
            GamePlayer player = this.game.getGame().getPlayer(event.getPlayer());

            WoolChestBreakEvent breakEvent = new WoolChestBreakEvent(this.game.getPlugin(), block, player);
            breakEvent.setCanceled(true); // This event is canceled by default.
            this.game.getPlugin().getEventBus().publish(breakEvent);

            if (breakEvent.isCanceled()) {
                event.setCanceled(true);

                if (player != null) {
                    player.sendError("You may not break wool chests.");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void dontProtectPlacedChests(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof InventoryHolder) {
            this.chests.put(block, new ChestSnapshot(false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void retainWoolChests(InventoryMoveItemEvent event) {
        this.registerWoolChest(event.getSource());
        this.registerWoolChest(event.getDestination());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void retainWoolChests(InventoryOpenEvent event) {
        this.registerWoolChest(event.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void retraceWoolChests(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        HumanEntity human = event.getPlayer();
        if (!(human instanceof Player)) {
            return;
        }

        GamePlayer player = this.game.getGame().getPlayer((Player) human);
        if (player == null || !player.isParticipating()) {
            return;
        }

        InventoryHolder inventoryHolder = inventory.getHolder();
        if (!(inventoryHolder instanceof BlockState)) {
            return;
        }

        Block block = ((BlockState) inventoryHolder).getBlock();

        ChestSnapshot snapshot = this.getChest(block);
        if (snapshot == null || !snapshot.woolChest || snapshot.snapshot.isEmpty()) {
            return;
        }

        boolean retraceWools = true;

        // Don't retrace wools if there are players viewing this chest.
        for (HumanEntity humanViewer : event.getViewers()) {
            if (humanViewer instanceof Player) {
                GamePlayer viewer = this.game.getGame().getPlayer((Player) humanViewer);
                if (viewer != null && viewer.isParticipating() && !viewer.equals(player)) {
                    retraceWools = false;
                }
            }
        }

        if (retraceWools) {
            WoolChestRetraceEvent retraceEvent = new WoolChestRetraceEvent(this.game.getPlugin(), block, inventory, player);
            this.game.getPlugin().getEventBus().publish(retraceEvent);

            if (!retraceEvent.isCanceled()) {
                for (Map.Entry<Integer, ItemStack> entry : snapshot.snapshot.entrySet()) {
                    // Copy the item to not edit the snapshot!
                    inventory.setItem(entry.getKey(), entry.getValue().clone());
                }
            }
        }
    }

    private void registerWoolChest(Inventory inventory) {
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (!(inventoryHolder instanceof BlockState)) {
            return;
        }

        Block block = ((BlockState) inventoryHolder).getBlock();

        ChestSnapshot snapshot = this.getChest(block);
        if (snapshot != null) {
            // We have already seen this chest, don't register it.
            return;
        }

        this.chests.put(block, snapshot = new ChestSnapshot(WoolUtils.containsAny(inventory)));
        if (snapshot.woolChest) {
            WoolChestRegisterEvent event = new WoolChestRegisterEvent(this.game.getPlugin(), block, inventory);
            this.game.getPlugin().getEventBus().publish(event);

            if (!event.isCanceled()) {
                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null && WoolUtils.isWool(item)) {
                        snapshot.snapshot.put(slot, item);
                    }
                }
            }
        }
    }

    private class ChestSnapshot {
        boolean woolChest;
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        ChestSnapshot(boolean woolChest) {
            this.woolChest = woolChest;
        }

        ChestSnapshot(Map<Integer, ItemStack> snapshot) {
            this(true);
            this.snapshot = snapshot;
        }
    }

    //
    // Wool Pickups
    //

    public Map.Entry<GoalHolder, Collection<WoolCapturable>> findWoolsFor(GamePlayer player) {
        for (Map.Entry<GoalHolder, Collection<WoolCapturable>> entry : this.wools.asMap().entrySet()) {
            if (entry.getKey().contains(player)) {
                return entry;
            }
        }

        return null;
    }

    public void pickupWool(GamePlayer player, ItemStack item, Wool wool) {
        if (wool == null) {
            return;
        }

        Map.Entry<GoalHolder, Collection<WoolCapturable>> wools = this.findWoolsFor(player);

        WoolCapturable result = null;
        for (WoolCapturable capturable : wools.getValue()) {
            if (capturable.getColor().equals(wool.getColor())) {
                if (!capturable.isCaptured()) {
                    result = capturable;
                }

                break;
            }
        }

        if (result != null) {
            WoolCapturablePickupEvent event = new WoolCapturablePickupEvent(this.game.getPlugin(), result, item, wool);
            this.game.getPlugin().getEventBus().publish(event);

            if (!event.isCanceled()) {
                double oldProgress = result.getProgress();

                result.getContributions().addContributor(player);
                wools.getKey().sendGoalMessage(result.getGoalInteractMessage(player.getDisplayName()));

                GoalProgressEvent.call(this.game.getPlugin(), result, player, oldProgress);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupWool(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) {
            return;
        }

        GamePlayer player = this.game.getGame().getPlayer((Player) human);
        if (player == null || !player.isParticipating()) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item != null) {
            this.pickupWool(player, item, WoolUtils.fromItem(item));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupWool(PlayerPickupItemEvent event) {
        GamePlayer player = this.game.getGame().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        Item item = event.getItem();
        if (item != null) {
            ItemStack itemStack = item.getItemStack();
            if (itemStack != null) {
                this.pickupWool(player, itemStack, WoolUtils.fromItem(itemStack));
            }
        }
    }
}
