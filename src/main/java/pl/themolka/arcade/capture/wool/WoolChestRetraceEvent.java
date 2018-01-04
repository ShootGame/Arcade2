package pl.themolka.arcade.capture.wool;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class WoolChestRetraceEvent extends WoolChestEvent implements Cancelable {
    private boolean cancel;
    private final Inventory content;
    private final GamePlayer player;

    public WoolChestRetraceEvent(ArcadePlugin plugin, Block chestBlock, Inventory content, GamePlayer player) {
        super(plugin, chestBlock);

        this.content = content;
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

    public Inventory getContent() {
        return this.content;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }
}
