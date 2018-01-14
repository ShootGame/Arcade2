package pl.themolka.arcade.capture.wool;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

// NOTE: This event is canceled by default!
public class WoolChestBreakEvent extends WoolChestEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer player;

    public WoolChestBreakEvent(ArcadePlugin plugin, Block chestBlock, GamePlayer player) {
        super(plugin, chestBlock);

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

    public GamePlayer getPlayer() {
        return this.player;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }
}
