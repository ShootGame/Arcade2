package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class GameEvent extends Event {
    private final Game game;

    public GameEvent(ArcadePlugin plugin, Game game) {
        super(plugin);

        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }
}
