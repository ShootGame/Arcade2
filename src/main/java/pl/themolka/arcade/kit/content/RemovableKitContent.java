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

package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Removable;

public interface RemovableKitContent<T> extends KitContent<T>, Removable<GamePlayer> {
    @Override
    default void apply(GamePlayer player) {
        this.attach(player, this.getResult());
    }

    @Override
    default void remove(GamePlayer player) {
        this.attach(player, defaultValue());
    }

    void attach(GamePlayer player, T value);

    T defaultValue();

    interface Config<T extends RemovableKitContent<?>, R> extends KitContent.Config<T, R> {
    }
}
