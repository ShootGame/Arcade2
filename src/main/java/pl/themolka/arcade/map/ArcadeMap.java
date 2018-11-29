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

package pl.themolka.arcade.map;

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameHolder;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Representation of a playable map based on the given {@link OfflineMap} and
 * {@link MapManifest}.
 */
public class ArcadeMap implements GameHolder {
    private final OfflineMap mapInfo;
    private final MapManifest manifest;
    private final ConfigContext config;

    private Reference<Game> game;
    private String worldName;

    public ArcadeMap(OfflineMap mapInfo, MapManifest manifest, ConfigContext config) {
        this.mapInfo = Objects.requireNonNull(mapInfo, "mapInfo cannot be null");
        this.manifest = Objects.requireNonNull(manifest, "manifest cannot be null");
        this.config = Objects.requireNonNull(config, "config cannot be null");
    }

    @Override
    public Game getGame() {
        return this.game != null ? this.game.get() : null;
    }

    @Override
    public ArcadeMap getMap() {
        return this;
    }

    public ConfigContext getConfig() {
        return this.config;
    }

    public OfflineMap getMapInfo() {
        return this.mapInfo;
    }

    public MapManifest getManifest() {
        return this.manifest;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setGame(Game game) {
        if (this.game == null || this.game.get() == null) {
            this.game = new WeakReference<>(game);
        }
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
