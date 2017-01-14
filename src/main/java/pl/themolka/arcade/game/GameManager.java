package pl.themolka.arcade.game;

import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.map.MapQueue;
import pl.themolka.arcade.map.OfflineMap;

import java.io.File;
import java.io.IOException;

public interface GameManager {
    Game createGame(ArcadeMap map) throws IOException;

    Game createGame(OfflineMap map) throws IOException, MapParserException;

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

    boolean isNextRestart();

    void resetPlayers(Game newGame);

    void setCurrentGame(Game currentGame);

    void setDefaultMaxGameId();

    void setGameId(int gameId);

    void setMaxGameId(int maxGameId);

    void setNextRestart(boolean nextRestart);

    void serializeGame(File file, Game game);


}
