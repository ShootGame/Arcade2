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

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class GameEvent extends Event implements GameHolder {
    private final Game game;

    @Deprecated
    public GameEvent(ArcadePlugin plugin) {
        this(plugin, plugin.getGames().getCurrentGame());
    }

    @Deprecated
    public GameEvent(ArcadePlugin plugin, Game game) {
        super(plugin);

        this.game = game;
    }

    public GameEvent(Game game) {
        super(game.getPlugin());

        this.game = game;
    }

    @Override
    public Game getGame() {
        return this.game;
    }
}
