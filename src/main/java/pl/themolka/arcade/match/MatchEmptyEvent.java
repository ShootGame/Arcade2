package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class MatchEmptyEvent extends MatchEvent implements Cancelable {
    private boolean cancel;
    private final MatchWinner participant;

    public MatchEmptyEvent(ArcadePlugin plugin, Match match, MatchWinner participant) {
        super(plugin, match);

        this.participant = participant;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public MatchWinner getParticipant() {
        return this.participant;
    }
}
