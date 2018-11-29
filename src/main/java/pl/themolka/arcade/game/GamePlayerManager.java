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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayerManager implements PlayerResolver {
    /** Storing online players. */
    private final Map<UUID, GamePlayer> online = new HashMap<>();
    /** Storing all players ever joined. */
    private final Map<UUID, GamePlayer> players = new HashMap<>();

    @Override
    public GamePlayer resolve(String username) {
        for (GamePlayer everPlayed : this.players.values()) {
            if (everPlayed.getUsername().equalsIgnoreCase(username)) {
                return everPlayed;
            }
        }

        return null;
    }

    @Override
    public GamePlayer resolve(UUID uniqueId) {
        return this.getPlayer(uniqueId);
    }

    public Collection<GamePlayer> getAllPlayers() {
        return this.players.values();
    }

    public Collection<GamePlayer> getOnlinePlayers() {
        return this.online.values();
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        return uniqueId != null ? this.players.get(uniqueId) : null;
    }

    public void playerJoin(GamePlayer join) {
        this.players.put(join.getUuid(), join);
        this.online.put(join.getUuid(), join);
    }

    public boolean playerQuit(GamePlayer quit) {
        return this.playerQuit(quit.getUuid()) != null;
    }

    public GamePlayer playerQuit(UUID quit) {
        return this.online.remove(quit);
    }

    public boolean unregister(GamePlayer unregister) {
        return this.unregister(unregister.getUuid()) != null;
    }

    public GamePlayer unregister(UUID uniqueId) {
        this.playerQuit(uniqueId);
        return this.players.remove(uniqueId);
    }
}
