package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

public class ServerCycleEvent extends CycleEvent {
    private final Game newGame;
    private final Game oldGame;

    public ServerCycleEvent(ArcadePlugin plugin, Game newGame, Game oldGame) {
        super(plugin, newGame.getMap().getMapInfo());

        this.newGame = newGame;
        this.oldGame = oldGame;
    }

    public Game getNewGame() {
        return this.newGame;
    }

    public ArcadeMap getNewMap() {
        return this.getNewGame().getMap();
    }

    public Game getOldGame() {
        return this.oldGame;
    }

    public ArcadeMap getOldMap() {
        if (this.getOldGame() != null) {
            return this.getOldGame().getMap();
        }

        return null;
    }
}
