package pl.themolka.arcade.leak.core;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;

public class CoreLeakableBreakEvent extends CoreLeakableEvent implements Cancelable {
    private Block block;
    private final GoalHolder breaker;
    private boolean cancel;
    private final GamePlayer player;

    public CoreLeakableBreakEvent(ArcadePlugin plugin, CoreLeakable leakable, GoalHolder breaker, Block block, GamePlayer player) {
        super(plugin, leakable);

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

    public GoalHolder getBreaker() {
        return this.breaker;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public boolean isBreakedByPlayer() {
        return this.player != null;
    }
}
