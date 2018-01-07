package pl.themolka.arcade.capture.wool;

import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class WoolPickupEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private final boolean firstPickup;
    private final ItemStack item;
    private final GamePlayer picker;
    private final org.bukkit.material.Wool woolItem;

    public WoolPickupEvent(ArcadePlugin plugin, Wool wool, boolean firstPickup, ItemStack item,
                           GamePlayer picker, org.bukkit.material.Wool woolItem) {
        super(plugin, wool);

        this.firstPickup = firstPickup;
        this.item = item;
        this.picker = picker;
        this.woolItem = woolItem;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public GamePlayer getPicker() {
        return this.picker;
    }

    public org.bukkit.material.Wool getWoolItem() {
        return this.woolItem;
    }

    public boolean isFirstPickup() {
        return this.firstPickup;
    }
}
