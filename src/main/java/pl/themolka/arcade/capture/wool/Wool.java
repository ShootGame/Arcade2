package pl.themolka.arcade.capture.wool;

import net.engio.mbassy.listener.Handler;
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
import pl.themolka.arcade.util.Color;

public class Wool extends Capturable implements Listener {
    public static final DyeColor DEFAULT_COLOR = DyeColor.WHITE;
    public static final String DEFAULT_GOAL_NAME = "Wool";

    private DyeColor color = DEFAULT_COLOR;
    private boolean craftable = false;
    private Region monument;

    public Wool(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Wool(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);
    }

    @Override
    public void capture(GoalHolder completer) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        String message = owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                completer.getTitle() + ChatColor.RESET + ChatColor.YELLOW + ".";

        this.game.getMatch().sendGoalMessage(message);
        this.setCompleted(completer, true);
    }

    @Override
    public String getColoredName() {
        return WoolUtils.coloredName(this.color) + " " + this.getDefaultName();
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        return ChatColor.GOLD + interact + ChatColor.YELLOW + " picked up " +
                owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    @Override
    public String getName() {
        return WoolUtils.name(this.color) + " " + this.getDefaultName();
    }

    @Override
    public void resetCapturable() {
    }

    public org.bukkit.material.Wool createBukkitWool() {
        return new org.bukkit.material.Wool(this.getColor());
    }

    public ChatColor getChatColor() {
        return Color.ofDye(this.color).toChat();
    }

    public DyeColor getColor() {
        return this.color;
    }

    public Region getMonument() {
        return this.monument;
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
        if (!WoolUtils.isWool(block, this.getColor()) || !this.getMonument().contains(block)) {
            return;
        }

        GamePlayer player = this.game.getGame().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCanceled(true);
            return;
        }

        event.setCanceled(true);
        if (this.isCaptured()) {
            player.sendError(ChatColor.GOLD + this.getColoredName() +
                    Messageable.INFO_COLOR + " has already been captured.");
        } else if (this.hasOwner() && this.getOwner().contains(player)) {
            player.sendError("You may not capture your own " + ChatColor.GOLD +
                    this.getColoredName() + Messageable.ERROR_COLOR + ".");
        } else {
            event.setCanceled(false);

            WoolPlaceEvent placeEvent = new WoolPlaceEvent(this.game.getPlugin(), this, player);
            this.game.getPlugin().getEventBus().publish(placeEvent);

            if (!placeEvent.isCanceled()) {
                this.capture(placeEvent.getCompleter());
            }
        }
    }
}