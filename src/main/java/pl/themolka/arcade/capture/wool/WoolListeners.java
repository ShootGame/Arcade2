package pl.themolka.arcade.capture.wool;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.team.PlayerLeaveTeamEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoolListeners implements Listener {
    private final CaptureGame game;

    private final Map<Block, ChestImage> chestImages = new HashMap<>();

    private final Multimap<GamePlayer, Wool> pickedUp = ArrayListMultimap.create();
    private final List<Wool> standaloneWools;
    private final Multimap<GoalHolder, Wool> wools;

    public WoolListeners(CaptureGame game, List<Wool> standaloneWools,
                                   Multimap<GoalHolder, Wool> wools) {
        this.game = game;

        this.standaloneWools = standaloneWools;
        this.wools = wools;
    }

    //
    // Chest Protection
    //

    public ChestImage getChestImage(Block block) {
        return this.chestImages.get(block);
    }

    @Handler(priority = Priority.LOWEST)
    public void dontBreakWoolChests(BlockTransformEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Block block = event.getBlock();

        ChestImage image = this.getChestImage(block);
        if (image != null && image.woolChest) {
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
            this.chestImages.put(block, new ChestImage(false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void retainWoolChests(InventoryMoveItemEvent event) {
        this.registerWoolChest(event.getSource().getHolder());
        this.registerWoolChest(event.getDestination().getHolder());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void retainWoolChests(InventoryOpenEvent event) {
        this.registerWoolChest(event.getInventory().getHolder());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void restoreWoolChests(InventoryCloseEvent event) {
        HumanEntity human = event.getPlayer();

        if (human instanceof Player) {
            GamePlayer player = this.game.getGame().getPlayer((Player) human);
            if (player == null) {
                return;
            }

            List<GamePlayer> viewers = new ArrayList<>();
            for (HumanEntity humanViewer : event.getViewers()) {
                if (humanViewer instanceof Player) {
                    GamePlayer viewer = this.game.getGame().getPlayer((Player) humanViewer);

                    if (viewer != null) {
                        viewers.add(viewer);
                    }
                }
            }

            this.restoreWoolChest(event.getInventory().getHolder(), player, viewers);
        }
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
            WoolChestRegisterEvent event = new WoolChestRegisterEvent(this.game.getPlugin(), block, inventory);
            this.game.getPlugin().getEventBus().publish(event);

            if (!event.isCanceled()) {
                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null && WoolUtils.isWool(item)) {
                        image.snapshot.put(slot, item);
                    }
                }
            }
        }
    }

    private void restoreWoolChest(InventoryHolder inventoryHolder, GamePlayer player, List<GamePlayer> viewers) {
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
        if (image == null || !image.woolChest || image.snapshot.isEmpty()) {
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
            WoolChestRetraceEvent retraceEvent = new WoolChestRetraceEvent(this.game.getPlugin(), block, inventory, player, viewers);
            this.game.getPlugin().getEventBus().publish(retraceEvent);

            if (!retraceEvent.isCanceled()) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack snapshotItem = image.snapshot.get(i);
                    if (snapshotItem == null) {
                        continue;
                    }

                    // Copy the item to not edit the snapshot!
                    inventory.setItem(i, snapshotItem.clone());
                }

                for (Map.Entry<Integer, ItemStack> entry : image.snapshot.entrySet()) {
                    inventory.setItem(entry.getKey(), entry.getValue().clone());
                }
            }
        }
    }

    private class ChestImage {
        boolean woolChest;
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        ChestImage(boolean woolChest) {
            this.woolChest = woolChest;
        }

        ChestImage(Map<Integer, ItemStack> snapshot) {
            this(true);
            this.snapshot = snapshot;
        }
    }

    //
    // Wool Pickups
    //

    public Map.Entry<GoalHolder, Collection<Wool>> findWoolsFor(GamePlayer player) {
        for (Map.Entry<GoalHolder, Collection<Wool>> entry : this.wools.asMap().entrySet()) {
            if (entry.getKey().contains(player)) {
                return entry;
            }
        }

        return null;
    }

    public void pickupWool(GamePlayer player, ItemStack item, org.bukkit.material.Wool wool) {
        if (wool == null) {
            return;
        }

        GoalHolder competitor = this.game.getMatch().findWinnerByPlayer(player);
        if (competitor == null) {
            return;
        }

        Map.Entry<GoalHolder, Collection<Wool>> wools = this.findWoolsFor(player);

        List<Wool> withStandaloneWools = new ArrayList<>(wools.getValue());
        withStandaloneWools.addAll(this.standaloneWools);

        Wool result = null;
        for (Wool capturable : withStandaloneWools) {
            if (capturable.getColor().equals(wool.getColor()) && capturable.isCompletableBy(competitor)) {
                // There can only be one wools with the same color.
                result = capturable;
                break;
            }
        }

        boolean firstPickup = !this.pickedUp.containsKey(player);

        if (result != null && !result.isCaptured()) {
            double oldProgress = result.getProgress();

            WoolPickupEvent event = new WoolPickupEvent(this.game.getPlugin(), result, firstPickup, item, player, wool);
            this.game.getPlugin().getEventBus().publish(event);

            if (firstPickup && !event.isCanceled()) {
                GamePlayer picker = event.getPicker();
                Wool picked = event.getWool();

                picked.getContributions().addContributor(picker);
                wools.getKey().sendGoalMessage(picked.getGoalInteractMessage(picker.getDisplayName()));

                this.pickedUp.put(picker, picked);
                GoalProgressEvent.call(this.game.getPlugin(), picked, picker, oldProgress);
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

    @Handler(priority = Priority.LAST)
    public void resetPickups(PlayerLeaveTeamEvent event) {
        this.pickedUp.removeAll(event.getGamePlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void resetPickups(PlayerDeathEvent event) {
        GamePlayer player = this.game.getGame().getPlayer(event.getEntity());
        if (player != null) {
            this.pickedUp.removeAll(player);
        }
    }
}
