package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class MatchStartEvent extends MatchEvent implements Cancelable {
    private boolean cancel;
    private final boolean forceStart;

    public MatchStartEvent(ArcadePlugin plugin, Match match, boolean forceStart) {
        super(plugin, match);

        this.forceStart = forceStart;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }
}
