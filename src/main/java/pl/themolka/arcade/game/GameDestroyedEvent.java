package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameDestroyedEvent extends GameEvent {
    public GameDestroyedEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }
}
