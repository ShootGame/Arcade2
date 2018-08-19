package pl.themolka.arcade.objective.wool;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.Objective;
import pl.themolka.arcade.region.AbstractRegion;

import java.util.Collections;
import java.util.List;

public class Wool extends Objective {
    private final DyeColor color;
    private final boolean craftable;
    private final AbstractRegion monument;

    private final WoolPickupTracker pickupTracker;

    protected Wool(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.color = config.color();
        this.craftable = config.craftable();
        this.monument = library.getOrDefine(game, config.monument().get());

        this.pickupTracker = new WoolPickupTracker(this);
    }

    @Override
    public void completeObjective(Participator completer, GamePlayer player) {
        this.getGame().sendGoalMessage(this.describeOwner() + this.describeObjective() +
                ChatColor.YELLOW + " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                player.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + ".");
        super.completeObjective(completer, player);
    }

    @Override
    public String getColoredName() {
        return this.hasName() ? super.getName()
                              : WoolUtils.coloredName(this.color) + " " + this.getDefaultName();
    }

    @Override
    public String getDefaultName() {
        return Config.DEFAULT_NAME;
    }

    @Override
    public List<Object> getEventListeners() {
        return Collections.singletonList(this.pickupTracker);
    }

    @Override
    public String getName() {
        return this.hasName() ? super.getName()
                              : WoolUtils.name(this.color) + " " + this.getDefaultName();
    }

    @Override
    public void resetObjective() {
        this.pickupTracker.resetAllPickups();
        super.resetObjective();
    }

    public org.bukkit.material.Wool createBukkitWool() {
        return new org.bukkit.material.Wool(this.color);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public AbstractRegion getMonument() {
        return this.monument;
    }

    public boolean isCraftable() {
        return this.craftable;
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

        GamePlayer player = this.getGame().resolve(event.getActor());
        if (player != null) {
            player.sendError("You may not craft " + ChatColor.GOLD + this.getColoredName() + Messageable.ERROR_COLOR + ".");
        }

        event.getInventory().setResult(null);
    }

    @Handler(priority = Priority.HIGHER)
    public void onWoolPlace(BlockTransformEvent event) {
        Block block = event.getBlock();
        if (!WoolUtils.isWool(block) || !this.monument.contains(block)) {
            return;
        }

        GamePlayer player = event.getGamePlayer();
        if (player == null) {
            // Endermans, TNTs and other entities are not permitted to place wools.
            event.setCanceled(true);
            return;
        }

        if (!WoolUtils.isWool(block, this.color)) {
            event.setCanceled(true);
            return;
        }

        event.setCanceled(true);
        if (this.isCompleted()) {
            player.sendError(ChatColor.GOLD + this.getColoredName() + Messageable.ERROR_COLOR + " has already been captured!");
        } else if (this.hasOwner() && this.getOwner().contains(player)) {
            player.sendError("You may not capture your own " + ChatColor.GOLD + this.getColoredName() + Messageable.ERROR_COLOR + "!");
        } else {
            Participator participator = this.getParticipatorResolver().resolve(player);
            if (participator == null) {
                return;
            }

            WoolPlaceEvent placeEvent = new WoolPlaceEvent(this, player, participator);
            this.getPlugin().getEventBus().publish(placeEvent);

            if (placeEvent.isCanceled()) {
                return;
            }

            event.setCanceled(false);
            this.completeObjective(placeEvent.getCompleter(), player);
        }
    }

    public interface Config extends Objective.Config<Wool> {
        boolean DEFAULT_IS_CRAFTABLE = false;
        String DEFAULT_NAME = "Wool";

        DyeColor color();
        default boolean craftable() { return DEFAULT_IS_CRAFTABLE; }
        Ref<AbstractRegion.Config<?>> monument();

        @Override
        default Wool create(Game game, Library library) {
            return new Wool(game, library, this);
        }
    }
}
