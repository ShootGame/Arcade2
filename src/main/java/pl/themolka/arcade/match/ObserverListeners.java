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
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.team.PlayerLeftTeamEvent;

public class ObserverListeners implements Listener {
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

    @EventHandler
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

    @EventHandler(priority = EventPriority.HIGHEST)
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
    public void onPlayerLeftTeam(PlayerLeftTeamEvent event) {
        if (event.getGamePlayer().isOnline() && event.getTeam().isObserving()) {
            event.getPlayer().getBukkit().setHealth(0.0D);
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
