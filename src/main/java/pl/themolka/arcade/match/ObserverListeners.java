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
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadePlayerMoveEvent;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;
import pl.themolka.arcade.team.PlayerLeftTeamEvent;

public class ObserverListeners implements Listener {
    public static final int BORDER_Y = 32;
    public static final PlayerTeleportEvent.TeleportCause TELEPORT_CAUSE = PlayerTeleportEvent.TeleportCause.SPECTATE;

    private final MatchGame game;

    public ObserverListeners(MatchGame game) {
        this.game = game;
    }

    @Handler(priority = Priority.NORMAL)
    public void onArcadePlayerMove(ArcadePlayerMoveEvent event) {
        ArcadeMap map = this.game.getGame().getMap();
        int y = event.getTo().getBlockY();

        if (y < 0 - BORDER_Y || y > map.getWorld().getMaxHeight() + BORDER_Y) {
            event.getPlayer().getBukkit().teleport(map.getSpawn(), TELEPORT_CAUSE);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isObserving(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockInventorySpy(PlayerInteractEvent event) {
        if (this.isObserving(event.getPlayer()) && event.getClickedBlock() != null && !event.getPlayer().isSneaking()) {
            this.handleInventorySpy(event.getPlayer(), event.getClickedBlock());
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
        if (this.isObserving(event.getDamager()) || this.isObserving(event.getEntity())) {
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventorySpy(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player && this.isObserving(event.getPlayer()) && !event.getPlayer().isSneaking()) {
            Player player = (Player) event.getRightClicked();

            if (!this.isObserving(player)) {
                this.handleInventorySpy(event.getPlayer(), player);
            }
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
    public void onPlayerJoinedObservers(PlayerJoinedTeamEvent event) {
        if (event.getGamePlayer().isOnline() && event.getTeam().isObservers()) {
            this.game.getMatch().getObserversKit().apply(event.getGamePlayer()); // apply kits

            Player bukkit = event.getPlayer().getBukkit();
            if (this.game.getMatch().isRunning()) {
                bukkit.setHealth(0.0D);
            }

            for (ArcadePlayer player : this.game.getPlugin().getPlayers()) {
                if (player.getGamePlayer() != null && player.getGamePlayer().canSee(event.getGamePlayer())) {
                    player.getBukkit().showPlayer(bukkit);
                } else {
                    player.getBukkit().hidePlayer(bukkit);
                }
            }
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerLeftObservers(PlayerLeftTeamEvent event) {
        if (event.getGamePlayer().isOnline() && event.getTeam().isObservers() && this.game.getMatch().isRunning()) {
            Player bukkit = event.getPlayer().getBukkit();
            for (ArcadePlayer player : this.game.getPlugin().getPlayers()) {
                if (player.getGamePlayer() != null && player.getGamePlayer().canSee(event.getGamePlayer())) {
                    player.getBukkit().showPlayer(bukkit);
                } else {
                    player.getBukkit().hidePlayer(bukkit);
                }
            }
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
        // FIXME
        if (block.getState() instanceof InventoryHolder) {
            observer.openInventory(((InventoryHolder) block.getState()).getInventory());
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
        return this.game.getMatch().isObserving(this.game.getGame().getPlayer(player.getUniqueId()));
    }
}
