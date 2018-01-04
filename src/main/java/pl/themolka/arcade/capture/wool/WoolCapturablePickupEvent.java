package pl.themolka.arcade.capture.wool;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class WoolCapturablePickupEvent extends WoolCapturableEvent implements Cancelable {
    private boolean cancel;
    private final ItemStack item;
    private final Wool woolItem;

    public WoolCapturablePickupEvent(ArcadePlugin plugin, WoolCapturable wool, ItemStack item, Wool woolItem) {
        super(plugin, wool);

        this.item = item;
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

    public Wool getWoolItem() {
        return this.woolItem;
    }
}
