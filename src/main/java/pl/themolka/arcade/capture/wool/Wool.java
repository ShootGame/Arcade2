package pl.themolka.arcade.capture.wool;

import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;

import java.util.Collections;
import java.util.List;

public class Wool extends Capturable implements Listener {
    public static final DyeColor DEFAULT_COLOR = DyeColor.WHITE;
    public static final String DEFAULT_GOAL_NAME = "Wool";

    private DyeColor color = DEFAULT_COLOR;
    private boolean craftable = false;
    private Region monument;
    private final WoolPickupTracker pickupTracker;

    public Wool(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Wool(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        this.pickupTracker = new WoolPickupTracker(game, this);
    }

    @Override
    public void capture(GoalHolder completer, GamePlayer player) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        String message = owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                player.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + ".";

        this.game.getMatch().sendGoalMessage(message);
        this.setCompleted(completer);
    }

    @Override
    public String getColoredName() {
        if (this.hasName()) {
            return this.getName();
        }

        return WoolUtils.coloredName(this.color) + " " + this.getDefaultName();
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public List<Object> getEventListeners() {
        return Collections.singletonList(this.getPickupTracker());
    }

    @Override
    public String getName() {
        if (this.hasName()) {
            super.getName();
        }

        return WoolUtils.name(this.color) + " " + this.getDefaultName();
    }

    @Override
    public void resetCapturable() {
        this.pickupTracker.resetAllPickups();
    }

    public org.bukkit.material.Wool createBukkitWool() {
        return new org.bukkit.material.Wool(this.getColor());
    }

    public DyeColor getColor() {
        return this.color;
    }

    public Region getMonument() {
        return this.monument;
    }

    public WoolPickupTracker getPickupTracker() {
        return this.pickupTracker;
    }

    public boolean isCraftable() {
        return this.craftable;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public void setCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    public void setMonument(Region monument) {
        this.monument = monument;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("completed", this.isCompleted())
                .append("completedBy", this.getCompletedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("color", this.color)
                .append("craftable", this.craftable)
                .build();
    }

    //
    // Messages
    //

    public String getPickupMessage(String picker) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        return ChatColor.GOLD + picker + ChatColor.YELLOW + " picked up " +
                owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    //
    // Listeners
    //

    @EventHandler(ignoreCancelled = true)
    public void onWoolCraft(PrepareItemCraftEvent event) {
        if (this.isCraftable()) {
            return;
        }

        ItemStack result = event.getInventory().getResult();
        if (result == null || !WoolUtils.isWool(result, this.color)) {
            return;
        }

        GamePlayer player = this.getGame().getPlayer(event.getActor());
        if (player != null) {
            player.sendError("You may not craft " + ChatColor.GOLD +
                    this.getColoredName() + Messageable.ERROR_COLOR + ".");
        }

        event.getInventory().setResult(null);
    }

    @Handler(priority = Priority.HIGHER)
    public void onWoolPlace(BlockTransformEvent event) {
        Block block = event.getBlock();
        if (!WoolUtils.isWool(block) || !this.getMonument().contains(block)) {
            return;
        }

        GamePlayer player = this.game.getGame().getPlayer(event.getPlayer());
        if (player == null) {
            // Endermans, TNTs and other entities are not permitted to place wools.
            event.setCanceled(true);
            return;
        }

        if (!WoolUtils.isWool(block, this.getColor())) {
            // We need to prevent "Only X may be placed here" message if it is a
            // correct wool. The wool should be handled by the correct listener
            // in the correct Wool instance.

            for (Capturable otherCapturable : this.game.getCapturables()) {
                if (otherCapturable instanceof Wool) {
                    Wool otherWool = (Wool) otherCapturable;

                    if (WoolUtils.isWool(block, otherWool.getColor()) &&
                            otherWool.getMonument().contains(block)) {
                        // This will be handled in its correct listener, return.
                        return;
                    }
                }
            }

            event.setCanceled(true);
            player.sendError("Only " + this.getColoredName() +
                    Messageable.ERROR_COLOR + " may be placed here!");
            return;
        }

        event.setCanceled(true);
        if (this.isCompleted()) {
            player.sendError(ChatColor.GOLD + this.getColoredName() +
                    Messageable.ERROR_COLOR + " has already been captured!");
        } else if (this.hasOwner() && this.getOwner().contains(player)) {
            player.sendError("You may not capture your own " + ChatColor.GOLD +
                    this.getColoredName() + Messageable.ERROR_COLOR + "!");
        } else {
            GoalHolder competitor = this.game.getMatch().findWinnerByPlayer(player);
            if (competitor == null) {
                return;
            }

            WoolPlaceEvent placeEvent = new WoolPlaceEvent(this.game.getPlugin(), this, competitor, player);
            this.game.getPlugin().getEventBus().publish(placeEvent);

            if (placeEvent.isCanceled()) {
                return;
            }

            event.setCanceled(false);
            this.capture(placeEvent.getCompleter(), player);
        }
    }
}
