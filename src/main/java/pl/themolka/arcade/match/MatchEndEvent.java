package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.event.Cancelable;

public class MatchEndEvent extends MatchEvent implements Cancelable {
    private boolean cancel;
    private final boolean forceEnd;
    private final MatchWinner winner;

    public MatchEndEvent(ArcadePlugin plugin, Match match, MatchWinner winner, boolean forceEnd) {
        super(plugin, match);

        this.forceEnd = forceEnd;
        this.winner = winner;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public MatchWinner getWinner() {
        return this.winner;
    }

    public boolean isForceEnd() {
        return this.forceEnd;
    }
}
