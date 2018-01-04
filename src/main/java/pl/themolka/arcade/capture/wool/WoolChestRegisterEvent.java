package pl.themolka.arcade.capture.wool;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class WoolChestRegisterEvent extends WoolChestEvent implements Cancelable {
    private boolean cancel;
    private final Inventory content;

    public WoolChestRegisterEvent(ArcadePlugin plugin, Block chestBlock, Inventory content) {
        super(plugin, chestBlock);

        this.content = content;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Inventory getContent() {
        return this.content;
    }
}
