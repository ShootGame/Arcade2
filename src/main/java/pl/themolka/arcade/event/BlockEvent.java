package pl.themolka.arcade.event;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;

public class BlockEvent extends Event {
    private final Block block;

    public BlockEvent(ArcadePlugin plugin, Block block) {
        super(plugin);

        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }
}
