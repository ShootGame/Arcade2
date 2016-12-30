package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class MatchEvent extends GameEvent {
    private final Match match;

    public MatchEvent(ArcadePlugin plugin, Match match) {
        super(plugin, match.getGame());

        this.match = match;
    }

    public Match getMatch() {
        return this.match;
    }
}
