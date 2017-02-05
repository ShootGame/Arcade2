package pl.themolka.arcade.leak;

import org.bukkit.block.Block;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.match.MatchWinner;

public class LeakableBreakEvent extends LeakableEvent implements Cancelable {
    private Block block;
    private final MatchWinner breaker;
    private boolean cancel;

    public LeakableBreakEvent(ArcadePlugin plugin, Leakable leakable, MatchWinner breaker, Block block) {
        super(plugin, leakable);

        this.block = block;
        this.breaker = breaker;
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

    public MatchWinner getBreaker() {
        return this.breaker;
    }
}
