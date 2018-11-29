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

package pl.themolka.arcade.cycle;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
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
