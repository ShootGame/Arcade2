package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameManager {
    private final ArcadePlugin plugin;

    private Game currentGame;

    public GameManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}
