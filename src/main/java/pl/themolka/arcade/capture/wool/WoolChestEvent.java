package pl.themolka.arcade.capture.wool;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class WoolChestEvent extends GameEvent {
    private final Block chestBlock;

    public WoolChestEvent(ArcadePlugin plugin, Block chestBlock) {
        super(plugin);

        this.chestBlock = chestBlock;
    }

    public Block getChestBlock() {
        return this.chestBlock;
    }
}
