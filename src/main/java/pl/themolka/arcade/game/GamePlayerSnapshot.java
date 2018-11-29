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

import pl.themolka.arcade.util.Snapshot;

import java.util.UUID;

public class GamePlayerSnapshot implements Snapshot<GamePlayer> {
    private final String displayName;
    private final boolean participating;
    private final String username;
    private final UUID uuid;

    public GamePlayerSnapshot(GamePlayer source) {
        this(source.getDisplayName(),
                source.isParticipating(),
                source.getUsername(),
                source.getUuid());
    }

    public GamePlayerSnapshot(String displayName,
                              boolean participating,
                              String username,
                              UUID uuid) {
        this.displayName = displayName;
        this.participating = participating;
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public Class<GamePlayer> getSnapshotType() {
        return GamePlayer.class;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isParticipating() {
        return this.participating;
    }
}
