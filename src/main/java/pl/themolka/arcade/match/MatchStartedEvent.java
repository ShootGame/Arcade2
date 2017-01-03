package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;

public class MatchStartedEvent extends MatchEvent {
    public MatchStartedEvent(ArcadePlugin plugin, Match match) {
        super(plugin, match);
    }
}
