package pl.themolka.arcade.event;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This event wraps all events related to the blocks. BlockTransformEvent is the securest way to protect blocks.
 */
public class BlockTransformEvent extends BlockEvent implements Cancelable {
    private final List<Block> blocks = new CopyOnWriteArrayList<>();
    private boolean cancel;
    private final ArcadePlayer player;
    private final org.bukkit.event.block.BlockEvent source;

    public BlockTransformEvent(ArcadePlugin plugin, Block block, ArcadePlayer player, org.bukkit.event.block.BlockEvent source) {
        this(plugin, Collections.singletonList(block), player, source);
    }

    public BlockTransformEvent(ArcadePlugin plugin, List<Block> blocks, ArcadePlayer player, org.bukkit.event.block.BlockEvent source) {
        super(plugin, source.getBlock());

        this.blocks.addAll(blocks);
        this.player = player;
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

    public GamePlayer getGamePlayer() {
        if (this.hasPlayer()) {
            return this.getPlayer().getGamePlayer();
        }

        return null;
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }

    public org.bukkit.event.block.BlockEvent getSource() {
        return this.source;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }
}
