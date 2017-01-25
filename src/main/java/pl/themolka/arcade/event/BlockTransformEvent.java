package pl.themolka.arcade.event;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

/**
 * This event wraps all events related to the blocks. BlockTransformEvent is the securest way to protect blocks.
 */
public class BlockTransformEvent extends BlockEvent implements Cancelable {
    private boolean cancel;
    private final MaterialData newState;
    private final ArcadePlayer player;
    private final org.bukkit.event.block.BlockEvent source;

    public BlockTransformEvent(ArcadePlugin plugin, Block block, MaterialData newState,
                               ArcadePlayer player, org.bukkit.event.block.BlockEvent source) {
        this(plugin, newState, player, source);
    }

    public BlockTransformEvent(ArcadePlugin plugin, MaterialData newState,
                               ArcadePlayer player,  org.bukkit.event.block.BlockEvent source) {
        super(plugin, source.getBlock());

        this.newState = newState;
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

    public GamePlayer getGamePlayer() {
        if (this.hasPlayer()) {
            return this.getPlayer().getGamePlayer();
        }

        return null;
    }

    public MaterialData getNewState() {
        return this.newState;
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
