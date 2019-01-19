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

import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Precise and playable map description. This class doesn't hold an
 * {@link OfflineMap}.
 */
public class MapManifest {
    public static final String FILENAME = "map.xml";

    private final Set<IGameModuleConfig<?>> modules;
    private final WorldInfo world;

    public MapManifest(Set<IGameModuleConfig<?>> modules, WorldInfo world) {
        this.modules = modules != null  ? modules   : new LinkedHashSet<>();
        this.world = world != null      ? world     : new WorldInfo();
    }

    public Set<IGameModuleConfig<?>> getModules() {
        return new LinkedHashSet<>(this.modules);
    }

    public WorldInfo getWorld() {
        return this.world;
    }
}
