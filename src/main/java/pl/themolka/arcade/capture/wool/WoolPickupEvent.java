package pl.themolka.arcade.capture.wool;

import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class WoolPickupEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private final ItemStack item;
    private final org.bukkit.material.Wool woolItem;

    public WoolPickupEvent(ArcadePlugin plugin, Wool wool, ItemStack item, org.bukkit.material.Wool woolItem) {
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

    public org.bukkit.material.Wool getWoolItem() {
        return this.woolItem;
    }
}
