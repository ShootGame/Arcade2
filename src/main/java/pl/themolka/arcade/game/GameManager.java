/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.game;

import pl.themolka.arcade.cycle.CycleCountdown;
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
