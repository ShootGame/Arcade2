package pl.themolka.arcade.game;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.map.queue.MapQueue;

import java.io.IOException;

public interface GameManager {
    Game createGame(ArcadeMap map) throws DOMException, IOException;

    Game createGame(OfflineMap map) throws DOMException, IOException;

    void cycle(OfflineMap target);

    default void cycleNext() {
        if (this.isNextRestart()) {
            this.cycleRestart();
        } else {
            this.cycle(null);
        }
    }

    void cycleRestart();

    void destroyGame(Game game);

    void fillDefaultQueue();

    Game getCurrentGame();

    CycleCountdown getCycleCountdown();

    int getGameId();

    int getMaxGameId();

    MapQueue getQueue();

    RestartCountdown getRestartCountdown();

    boolean isNextRestart();

    void resetPlayers(Game newGame);

    void setCurrentGame(Game currentGame);

    void setDefaultMaxGameId();

    void setGameId(int gameId);

    void setMaxGameId(int maxGameId);

    void setNextRestart(boolean nextRestart);
}
