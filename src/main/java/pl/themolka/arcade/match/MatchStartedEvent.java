package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;

public class MatchStartedEvent extends MatchEvent {
    private final boolean forceStart;

    public MatchStartedEvent(ArcadePlugin plugin, Match match, boolean forceStart) {
        super(plugin, match);

        this.forceStart = forceStart;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }
}
