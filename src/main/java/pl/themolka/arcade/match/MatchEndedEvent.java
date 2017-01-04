package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;

public class MatchEndedEvent extends MatchEvent {
    private final boolean forceEnd;
    private final MatchWinner winner;

    public MatchEndedEvent(ArcadePlugin plugin, Match match, MatchWinner winner, boolean forceEnd) {
        super(plugin, match);

        this.forceEnd = forceEnd;
        this.winner = winner;
    }

    public MatchWinner getWinner() {
        return this.winner;
    }

    public boolean isForceEnd() {
        return this.forceEnd;
    }
}
