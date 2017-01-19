package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class MatchStartCountdownEvent extends MatchEvent implements Cancelable {
    private boolean cancel;
    private MatchStartCountdown countdown;

    public MatchStartCountdownEvent(ArcadePlugin plugin, Match match, MatchStartCountdown countdown) {
        super(plugin, match);

        this.countdown = countdown;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public MatchStartCountdown getCountdown() {
        return this.countdown;
    }
}
