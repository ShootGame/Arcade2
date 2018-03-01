package pl.themolka.arcade.match;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupExperienceEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.command.Command;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;

/**
 * Listeners related to manipulate observers. Observers are the unique players
 * on the server which cannot interact with the map, gameplay and other players.
 * Observers are defined with a {@link Observers} class instance.
 */
public class ObserverListeners implements Listener {
    public static final int BORDER_Y = 32;
    public static final String PLAY_COMMAND = "join";
    public static final PlayerTeleportEvent.TeleportCause TELEPORT_CAUSE =
            PlayerTeleportEvent.TeleportCause.SPECTATE;

    private final MatchGame game;

    public ObserverListeners(MatchGame game) {
        this.game = game;
    }

    //
    // Inventory Spy
    //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockInventorySpy(PlayerInteractEvent event) {
        if (this.isObserving(event.getPlayer()) &&
                event.getClickedBlock() != null &&
                !event.getPlayer().isSneaking()) {
            this.handleInventorySpy(event.getPlayer(), event.getClickedBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInventorySpy(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player &&
                this.isObserving(event.getPlayer()) &&
                !event.getPlayer().isSneaking()) {
            Player player = (Player) event.getRightClicked();

            if (!this.isObserving(player)) {
                this.handleInventorySpy(event.getPlayer(), player);
            }
        }
    }

    private void handleInventorySpy(Player observer, Block block) {
        BlockState state = block.getState();
        if (state instanceof InventoryHolder) {
            observer.openInventory(((InventoryHolder) state).getInventory());
        }
    }

    private void handleInventorySpy(Player observer, Player player) {
        int size = player.getInventory().getSize();
        String title = player.getDisplayName() + ChatColor.GRAY + "'s inventory";

        Inventory spy = this.game.getServer().createInventory(null, size, title);
        spy.setContents(player.getInventory().getContents());
        observer.openInventory(spy);
    }

    //
    // Kit and Visibility Filtering
    //

    @Handler(priority = Priority.NORMAL)
    public void onPlayerJoinedObservers(ObserversJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (player.isOnline()) {
            if (this.game.getMatch().isRunning()) {
                player.kill();
            }

            this.game.getMatch().getObserversKit().apply(player);
            // ^ apply kits

            player.refreshVisibilityArcadePlayer(this.game.getPlugin().getPlayers());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerLeaveObservers(ObserversLeaveEvent event) {
        if (event.getGamePlayer().isOnline() && this.game.getMatch().isRunning()) {
            event.getGamePlayer().refreshVisibilityArcadePlayer(this.game.getPlugin().getPlayers());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (player.isOnline() && !player.isParticipating()) {
            player.getPlayer().clearInventory(true);

            this.game.getMatch().getObserversKit().apply(player);
            player.getBukkit().updateInventory();
            // ^ apply kits
        }
    }

    //
    // Miscellaneous
    //

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchWindowOpen(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            // Deny all actions which are not right clicks.
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.isSimilar(ObserversKit.PLAY)) {
            GamePlayer player = this.game.getGame().getPlayer(event.getPlayer());
            if (player == null) {
                return;
            }

            PlayMatchWindow window = this.game.getMatch().getPlayWindow();
            if (window != null && window.open(player)) {
                return;
            }

            // The PlayerMatchWindow is not set. As we cannot handle the window
            // join the given player to match with the '/join' command instead.
            Commands commands = this.game.getPlugin().getCommands();
            Command command = commands.getCommand(PLAY_COMMAND);

            if (command != null) {
                commands.handleCommand(player, command, command.getCommand(), null);
                return;
            }

            player.sendError("Could not join the match right now. Did you defined format module?");
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isObserving(event.getVictimBukkit())) {
            event.setAutoRespawn(true);
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerVoidTeleport(PlayerMoveEvent event) {
        Player bukkit = event.getBukkitPlayer();
        if (!event.isCanceled() && this.isObserving(bukkit)) {
            ArcadeMap map = this.game.getGame().getMap();
            int y = event.getTo().getBlockY();

            // Teleport observers when they are in the void.
            if (y < 0 - BORDER_Y || y > map.getWorld().getMaxHeight() + BORDER_Y) {
                event.getPlayer().getBukkit().teleport(map.getManifest().getWorld().getSpawn(), TELEPORT_CAUSE);
            }
        }
    }

    //
    // Fix night vision effect when observers are in the void.
    //

    @Handler(priority = Priority.NORMAL)
    public void fixVoidNightVision(PlayerMoveEvent event) {
        if (!event.isCanceled()) {
            this.handleFixVoidNightVision(event.getBukkitPlayer(),
                    event.getFrom().getBlockY(), event.getTo().getBlockY());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void fixVoidNightVision(PlayerTeleportEvent event) {
        this.handleFixVoidNightVision(event.getPlayer(),
                event.getFrom().getBlockY(), event.getTo().getBlockY());
    }

    private void handleFixVoidNightVision(Player bukkit, int from, int to) {
        if (this.isObserving(bukkit)) {
            if (from < 0 && to >= 0) {
                bukkit.addPotionEffect(ObserversKit.NIGHT_VISION, true);
            } else if (from >= 0 && to < 0) {
                bukkit.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }

    //
    // Blocking
    //

    // These listeners are in the LOW event priority. The LOWEST is reserved
    // for internal features for observer, eg. opening spy inventories.

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.isObserving(event.getDamager()) ||
                this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (this.isObserving(event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.isObserving(event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (this.isObserving(entity)) {
                event.setIntensity(entity, 0.0D);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (this.isObserving(event.getAttacker())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (this.isObserving(event.getEntered())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private boolean isObserving(Entity entity) {
        return entity instanceof Player && this.isObserving((Player) entity);
    }

    private boolean isObserving(Player player) {
        return this.game.getMatch().isObserving(this.game.getGame().getPlayer(player));
    }
}
