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

package pl.themolka.arcade.spawn;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.parser.Silent;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.RegionParser;
import pl.themolka.arcade.region.UnionRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SpawnParser<T extends Spawn.Config<?>> extends ConfigParser<T>
                                                             implements InstallableParser {
    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
    }

    private abstract static class DirectionalParser<T extends Spawn.Config<?>> extends SpawnParser<T>
                                                                               implements InstallableParser {
        private Parser<Float> yawParser;
        private Parser<Float> pitchParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.yawParser = library.type(Float.class);
            this.pitchParser = library.type(Float.class);
        }

        protected float parseYaw(Context context, Node node) throws ParserException {
            return this.yawParser.parse(context, node.property("yaw")).orDefault(Directional.Config.DEFAULT_YAW);
        }

        protected float parsePitch(Context context, Node node) throws ParserException {
            return this.pitchParser.parse(context, node.property("pitch")).orDefault(Directional.Config.DEFAULT_PITCH);
        }
    }

    //
    // Auto
    //

    @Produces(Spawn.Config.class)
    public static class Auto extends SpawnParser {
        private NestedParserMap<SpawnParser<?>> nested;
        private SpawnParser<?> defaultParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);

            this.nested = new NestedParserMap<>(library);
            this.nested.scan(SpawnParser.class);

            this.defaultParser = library.of(RegionExact.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("spawn");
        }

        @Override
        protected Result<?> parseNode(Context context, Node node, String name, String value) throws ParserException {
            SpawnParser<?> parser = this.nested.parse(name);
            if (parser != null) {
                return parser.parseWithDefinition(context, node, name, value);
            }

            return this.defaultParser.parseWithDefinition(context, node, name, value);
        }
    }

    //
    // Multi
    //

    @NestedParserName("multi")
    @Produces(MultiSpawn.Config.class)
    public static class Multi extends SpawnParser<MultiSpawn.Config>
                              implements InstallableParser {
        private Parser<Spawn.Config> spawnParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.spawnParser = library.type(Spawn.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("multi-spawn");
        }

        @Override
        protected Result<MultiSpawn.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);

            List<Spawn.Config<?>> spawns = new ArrayList<>();
            for (Node spawnNode : node.children()) {
                spawns.add(this.spawnParser.parse(context, spawnNode).orFail());
            }

            if (ParserUtils.ensureNotEmpty(spawns)) {
                throw this.fail(node, name, value, "No spawns defined");
            }

            return Result.fine(node, name, value, new MultiSpawn.Config() {
                public String id() { return id; }
                public Ref<List<Spawn.Config<?>>> spawns() { return Ref.ofProvided(spawns); }
            });
        }
    }

    //
    // Region (Union)
    //

    @NestedParserName({"region", "union", "region-union", "regionunion"})
    @Produces(RegionSpawnVector.Config.class)
    public static class Region extends DirectionalParser<RegionSpawnVector.Config>
                               implements InstallableParser {
        private Parser<UnionRegion.Config> regionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.regionParser = library.of(RegionParser.Union.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("multi-region spawn");
        }

        @Override
        protected Result<RegionSpawnVector.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            AbstractRegion.Config region = this.regionParser.parseWithDefinition(context, node, name, value).orFail();
            float yaw = this.parseYaw(context, node);
            float pitch = this.parsePitch(context, node);

            return Result.fine(node, name, value, new RegionSpawnVector.Config() {
                public String id() { return id; }
                public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
                public float yaw() { return yaw; }
                public float pitch() { return pitch; }
            });
        }
    }

    //
    // Region Exact
    //

    @Silent
    @Produces(RegionSpawnVector.Config.class)
    public static class RegionExact extends DirectionalParser<RegionSpawnVector.Config>
                                    implements InstallableParser {
        private Parser<AbstractRegion.Config> regionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.regionParser = library.type(AbstractRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("single region spawn");
        }

        @Override
        protected Result<RegionSpawnVector.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            AbstractRegion.Config<?> region = this.regionParser.parseWithDefinition(context, node, name, value).orFail();
            float yaw = this.parseYaw(context, node);
            float pitch = this.parsePitch(context, node);

            return Result.fine(node, name, value, new RegionSpawnVector.Config() {
                public String id() { return id; }
                public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
                public float yaw() { return yaw; }
                public float pitch() { return pitch; }
            });
        }
    }
}
