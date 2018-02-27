package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class GameEvent extends Event implements GameHolder {
    private final Game game;

    public GameEvent(ArcadePlugin plugin) {
        this(plugin, plugin.getGames().getCurrentGame());
    }

    public GameEvent(ArcadePlugin plugin, Game game) {
        super(plugin);

        this.game = game;
    }

    @Override
    public Game getGame() {
        return this.game;
    }
}
