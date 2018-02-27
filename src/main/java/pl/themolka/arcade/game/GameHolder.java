package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

/**
 * Something that can hold a {@link Game}.
 */
public interface GameHolder {
    Game getGame();

    default ArcadeMap getMap() {
        Game game = this.getGame();
        return game != null ? game.getMap() : null;
    }

    default ArcadePlugin getPlugin() {
        Game game = this.getGame();
        return game != null ? game.getPlugin() : null;
    }
}
