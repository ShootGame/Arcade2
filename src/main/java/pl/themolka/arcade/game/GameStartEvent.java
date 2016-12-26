package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameStartEvent extends GameEvent {
    public GameStartEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }
}
