package pl.themolka.arcade.capture.wool;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.team.PlayerLeaveTeamEvent;

public class WoolPickupTracker implements Listener {
    private final CaptureGame game;
    private final Wool wool;

    private final Multimap<GoalHolder, GamePlayer> pickups = ArrayListMultimap.create();

    public WoolPickupTracker(CaptureGame game, Wool wool) {
        this.game = game;
        this.wool = wool;
    }

    public Wool getWool() {
        return this.wool;
    }

    public void pickup(GamePlayer picker, ItemStack item) {
        GoalHolder competitor = this.game.getMatch().findWinnerByPlayer(picker);
        if (competitor == null || this.wool.isCompleted() || !this.wool.isCompletableBy(competitor)) {
            return;
        }

        boolean firstPickup = !this.pickups.containsValue(picker);
        boolean firstCompetitorPickup = !this.pickups.containsKey(competitor);
        double oldProgress = this.wool.getProgress();

        WoolPickupEvent event = new WoolPickupEvent(this.game.getPlugin(), this.wool, competitor, firstPickup, firstCompetitorPickup, item, picker);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled() && !this.pickups.containsValue(picker)) {
            this.wool.getContributions().addContributor(picker);
            competitor.sendGoalMessage(this.wool.getPickupMessage(picker.getDisplayName()));

            this.pickups.put(competitor, picker);
            GoalProgressEvent.call(this.game.getPlugin(), this.wool, picker, oldProgress);
        }
    }

    public void resetAllPickups() {
        this.pickups.clear();
    }

    public void resetPickups(GamePlayer player) {
        GoalHolder competitor = this.game.getMatch().findWinnerByPlayer(player);
        if (competitor != null) {
            this.pickups.remove(competitor, player);
        }
    }

    //
    // Listeners
    //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupBetweenInventories(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        if (human instanceof Player) {
            GamePlayer picker = this.game.getGame().getPlayer((Player) human);
            if (!picker.isParticipating()) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            if (item != null && WoolUtils.isWool(item, this.wool.getColor())) {
                this.pickup(picker, item);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupFromGround(PlayerPickupItemEvent event) {
        GamePlayer picker = this.game.getGame().getPlayer(event.getPlayer());
        if (picker == null || !picker.isParticipating()) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if (item != null && WoolUtils.isWool(item, this.wool.getColor())) {
            this.pickup(picker, item);
        }
    }

    @Handler(priority = Priority.LAST)
    public void resetPickups(PlayerLeaveTeamEvent event) {
        if (!event.isCanceled()) {
            this.resetPickups(event.getGamePlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void resetPickups(PlayerDeathEvent event) {
        GamePlayer picker = this.game.getGame().getPlayer(event.getEntity());
        if (picker != null) {
            this.resetPickups(picker);
        }
    }
}
