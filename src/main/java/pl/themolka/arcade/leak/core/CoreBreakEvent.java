package pl.themolka.arcade.leak.core;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class CoreBreakEvent extends CoreEvent implements Cancelable {
    private Block block;
    private final Participator breaker;
    private boolean cancel;
    private final GamePlayer player;

    public CoreBreakEvent(ArcadePlugin plugin, Core core, Participator breaker, Block block, GamePlayer player) {
        super(plugin, core);

        this.block = block;
        this.breaker = breaker;
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

    public Block getBlock() {
        return this.block;
    }

    public Participator getBreaker() {
        return this.breaker;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public boolean isBreakedByPlayer() {
        return this.player != null;
    }
}
