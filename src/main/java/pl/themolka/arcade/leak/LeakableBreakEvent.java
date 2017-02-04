package pl.themolka.arcade.leak;

import org.bukkit.util.Vector;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.match.MatchWinner;

public class LeakableBreakEvent extends LeakableEvent implements Cancelable {
    private final MatchWinner breaker;
    private boolean cancel;
    private Vector vector;

    public LeakableBreakEvent(ArcadePlugin plugin, Leakable leakable, MatchWinner breaker, Vector vector) {
        super(plugin, leakable);

        this.breaker = breaker;
        this.vector = vector;
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

    public Vector getVector() {
        return this.vector;
    }
}
