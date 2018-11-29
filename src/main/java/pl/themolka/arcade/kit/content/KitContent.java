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

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;

public interface KitContent<T> extends PlayerApplicable {
    default void applyIfApplicable(GamePlayer player) {
        if (this.isApplicable(player)) {
            this.apply(player);
        }
    }

    T getResult();

    default boolean isApplicable(GamePlayer player) {
        return test(player);
    }

    //
    // Player Testing Methods
    //

    static boolean test(GamePlayer player) {
        return player != null && player.isOnline();
    }

    static boolean testBukkit(GamePlayer player) {
        return test(player) && player.getBukkit() != null;
    }

    interface Config<T extends KitContent<?>, R> extends IGameConfig<T> {
        Ref<R> result();
    }
}
