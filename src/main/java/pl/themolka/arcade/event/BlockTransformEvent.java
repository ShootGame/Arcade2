package pl.themolka.arcade.event;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.event.Cancelable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockTransformEvent extends BlockEvent implements Cancelable {
    private final List<Block> blocks = new CopyOnWriteArrayList<>();
    private boolean cancel;
    private final org.bukkit.event.block.BlockEvent source;

    public BlockTransformEvent(ArcadePlugin plugin, Block block, org.bukkit.event.block.BlockEvent source) {
        this(plugin, Collections.singletonList(block), source);
    }

    public BlockTransformEvent(ArcadePlugin plugin, List<Block> blocks, org.bukkit.event.block.BlockEvent source) {
        super(plugin, source.getBlock());

        this.blocks.addAll(blocks);
        this.source = source;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public org.bukkit.event.block.BlockEvent getSource() {
        return this.source;
    }
}
