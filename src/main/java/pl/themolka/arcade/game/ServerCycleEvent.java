package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

public class ServerCycleEvent extends CycleEvent {
    private final Game newGame;
    private final ArcadeMap newMap;

    public ServerCycleEvent(ArcadePlugin plugin, Game newGame) {
        super(plugin, newGame.getMap().getMapInfo());

        this.newGame = newGame;
        this.newMap = newGame.getMap();
    }

    public Game getNewGame() {
        return this.newGame;
    }

    public ArcadeMap getNewMap() {
        return this.newMap;
    }
}
