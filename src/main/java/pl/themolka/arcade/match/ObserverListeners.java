package pl.themolka.arcade.match;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectExtendEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
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
import pl.themolka.arcade.command.Command;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.map.ArcadeMap;
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockInventorySpy(PlayerInteractEvent event) {
        if (this.isObserving(event.getPlayer()) &&
                event.getClickedBlock() != null &&
                !event.getPlayer().isSneaking()) {
            this.handleInventorySpy(event.getPlayer(), event.getClickedBlock());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.isObserving(event.getDamager()) ||
                this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (this.isObserving(event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.isObserving(event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventorySpy(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player &&
                this.isObserving(event.getPlayer()) &&
                !event.getPlayer().isSneaking()) {
            Player player = (Player) event.getRightClicked();

            if (!this.isObserving(player)) {
                this.handleInventorySpy(event.getPlayer(), player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMatchWindowOpen(PlayerInteractEvent event) {
        if (event.getItem() != null &&
                event.getItem().equals(ObserversKit.PLAY)) {
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
                commands.handleCommand(player, command,
                                       command.getCommand(), null);
                return;
            }

            player.sendError("Could not join the match right now. " +
                             "Did you defined format module?");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerJoinedObservers(ObserversJoinEvent event) {
        if (event.getGamePlayer().isOnline()) {
            this.game.getMatch().getObserversKit().apply(event.getGamePlayer());
            // ^ apply kits

            Player bukkit = event.getPlayer().getBukkit();
            if (this.game.getMatch().isRunning()) {
                bukkit.setHealth(0.0D);
            }

            event.getGamePlayer().refreshVisibility(
                    this.game.getPlugin().getPlayers());
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerLeaveObservers(ObserversLeaveEvent event) {
        if (event.getGamePlayer().isOnline() &&
                this.game.getMatch().isRunning()) {
            event.getGamePlayer().refreshVisibility(
                    this.game.getPlugin().getPlayers());
        }
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerVoidTeleport(PlayerMoveEvent event) {
        if (this.isObserving(event.getPlayer().getBukkit())) {
            ArcadeMap map = this.game.getGame().getMap();
            int y = event.getTo().getBlockY();

            // teleport observers when they are in the void
            if (y < 0 - BORDER_Y || y > map.getWorld()
                    .getMaxHeight() + BORDER_Y) {
                event.getPlayer().getBukkit().teleport(
                        map.getSpawn(), TELEPORT_CAUSE);
            }
        }
    }

    @EventHandler
    public void onPotionEffectAdd(PotionEffectAddEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffectExtend(PotionEffectExtendEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffectRemove(PotionEffectRemoveEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (this.isObserving(entity)) {
                event.setIntensity(entity, 0.0D);
            }
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (this.isObserving(event.getAttacker())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (this.isObserving(event.getEntered())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (this.isObserving(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private void handleInventorySpy(Player observer, Block block) {
        if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();
            observer.openInventory(holder.getInventory());
        }
    }

    private void handleInventorySpy(Player observer, Player player) {
        int size = player.getInventory().getSize();
        String title = player.getDisplayName() + ChatColor.GRAY + "'s inventory";

        Inventory spy = this.game.getServer().createInventory(null, size, title);
        spy.setContents(player.getInventory().getContents());
        observer.openInventory(spy);
    }

    private boolean isObserving(Entity entity) {
        return entity instanceof Player && this.isObserving((Player) entity);
    }

    private boolean isObserving(Player player) {
        return this.game.getMatch().isObserving(
                this.game.getGame().getPlayer(player));
    }
}
