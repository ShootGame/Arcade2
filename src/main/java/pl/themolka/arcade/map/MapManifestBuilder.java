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

import org.apache.commons.lang3.builder.Builder;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class MapManifestBuilder implements Builder<MapManifest> {
    private Set<IGameModuleConfig<?>> modules;
    private WorldInfo worldInfo;

    @Override
    public MapManifest build() {
        return new MapManifest(this.modules(),
                               this.worldInfo());
    }

    public Set<IGameModuleConfig<?>> modules() {
        return new LinkedHashSet<>(this.modules);
    }

    public MapManifestBuilder modules(Set<IGameModuleConfig<?>> modules) {
        this.modules = modules;
        return this;
    }

    public WorldInfo worldInfo() {
        return this.worldInfo;
    }

    public MapManifestBuilder worldInfo(WorldInfo worldInfo) {
        this.worldInfo = worldInfo;
        return this;
    }
}
