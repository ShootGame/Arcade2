package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameStopEvent extends GameEvent {
    public GameStopEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }
}
