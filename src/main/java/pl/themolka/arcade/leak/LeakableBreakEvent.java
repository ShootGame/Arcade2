package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.match.MatchWinner;

public class LeakableBreakEvent extends LeakableEvent implements Cancelable {
    private final MatchWinner breaker;
    private boolean cancel;

    public LeakableBreakEvent(ArcadePlugin plugin, Leakable leakable, MatchWinner breaker) {
        super(plugin, leakable);

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

    public MatchWinner getBreaker() {
        return this.breaker;
    }
}
