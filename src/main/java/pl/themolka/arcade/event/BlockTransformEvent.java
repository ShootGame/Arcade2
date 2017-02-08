package pl.themolka.arcade.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

/**
 * This event wraps all events related to the blocks. BlockTransformEvent is the securest way to protect blocks.
 */
public class BlockTransformEvent extends BlockEvent implements Cancelable {
    private boolean cancel;
    private final org.bukkit.event.Event cause;
    private final BlockState newState;
    private final BlockState oldState;
    private final ArcadePlayer player;

    public BlockTransformEvent(ArcadePlugin plugin, Event cause, BlockState newState, BlockState oldState) {
        this(plugin, cause, newState, oldState, null);
    }

    public BlockTransformEvent(ArcadePlugin plugin, Event cause, BlockState newState,
                               BlockState oldState, ArcadePlayer player) {
        this(plugin, oldState.getBlock(), cause, newState, oldState, player);
    }

    public BlockTransformEvent(ArcadePlugin plugin, Block block, Event cause, BlockState newState,
                               BlockState oldState, ArcadePlayer player) {
        super(plugin, block);

        this.cause = cause;
        this.newState = newState;
        this.oldState = oldState;
        this.player = player;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Event getCause() {
        return this.cause;
    }

    public GamePlayer getGamePlayer() {
        if (this.hasPlayer()) {
            return this.getPlayer().getGamePlayer();
        }

        return null;
    }

    public BlockState getNewState() {
        return this.newState;
    }

    public BlockState getOldState() {
        return this.oldState;
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }
}
