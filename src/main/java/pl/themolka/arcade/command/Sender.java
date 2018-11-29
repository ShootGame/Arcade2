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

package pl.themolka.arcade.command;

import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.Locale;
import java.util.UUID;

public interface Sender extends Messageable {
    Locale DEFAULT_LOCALE = Locale.US;

    default GamePlayer getGamePlayer() {
        if (this.isConsole()) {
            return null;
        }

        return this.getPlayer().getGamePlayer();
    }

    default Locale getLocale() {
        return DEFAULT_LOCALE;
    }

    ArcadePlayer getPlayer();

    String getUsername();

    UUID getUuid();

    boolean hasPermission(String permission);

    boolean isConsole();
}
