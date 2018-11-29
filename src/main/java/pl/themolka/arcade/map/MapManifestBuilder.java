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
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class MapManifestBuilder implements Builder<MapManifest> {
    private Set<IGameModuleConfig<?>> modules;
    private Node source;
    private WorldInfo world;

    @Override
    public MapManifest build() {
        return new MapManifest(this.modules(),
                               this.source(),
                               this.world());
    }

    public Set<IGameModuleConfig<?>> modules() {
        return new LinkedHashSet<>(this.modules);
    }

    public MapManifestBuilder modules(Set<IGameModuleConfig<?>> modules) {
        this.modules = modules;
        return this;
    }

    public Node source() {
        return this.source;
    }

    public MapManifestBuilder source(Node source) {
        this.source = source;
        return this;
    }

    public WorldInfo world() {
        return this.world;
    }

    public MapManifestBuilder world(WorldInfo world) {
        this.world = world;
        return this;
    }
}
