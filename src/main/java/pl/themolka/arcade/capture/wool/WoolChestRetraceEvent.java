package pl.themolka.arcade.capture.wool;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class WoolChestRetraceEvent extends WoolChestEvent implements Cancelable {
    private boolean cancel;
    private final Inventory content;
    private final GamePlayer player;
    private final List<GamePlayer> viewers;

    public WoolChestRetraceEvent(ArcadePlugin plugin, Block chestBlock, Inventory content, GamePlayer player, List<GamePlayer> viewers) {
        super(plugin, chestBlock);

        this.content = content;
        this.player = player;
        this.viewers = viewers;
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

    public List<GamePlayer> getViewers() {
        return new ArrayList<>(this.viewers);
    }

    public boolean isViewing(GamePlayer player) {
        return this.viewers.contains(player);
    }
}
