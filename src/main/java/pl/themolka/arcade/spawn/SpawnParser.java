package pl.themolka.arcade.spawn;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
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
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
    }

    private abstract static class DirectionalParser<T extends Spawn.Config<?>> extends SpawnParser<T>
                                                                               implements InstallableParser {
        private Parser<Float> yawParser;
        private Parser<Float> pitchParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.yawParser = context.type(Float.class);
            this.pitchParser = context.type(Float.class);
        }

        protected float parseYaw(Node node) throws ParserException {
            return this.yawParser.parse(node.property("yaw")).orDefault(Directional.Config.DEFAULT_YAW);
        }

        protected float parsePitch(Node node) throws ParserException {
            return this.pitchParser.parse(node.property("pitch")).orDefault(Directional.Config.DEFAULT_PITCH);
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);

            this.nested = new NestedParserMap<>(context);
            this.nested.scan(SpawnParser.class);

            this.defaultParser = context.of(RegionExact.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("spawn");
        }

        @Override
        protected Result<?> parseNode(Node node, String name, String value) throws ParserException {
            SpawnParser<?> parser = this.nested.parse(name);
            if (parser != null) {
                return parser.parseWithDefinition(node, name, value);
            }

            return this.defaultParser.parseWithDefinition(node, name, value);
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.spawnParser = context.type(Spawn.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("multi-spawn");
        }

        @Override
        protected Result<MultiSpawn.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);

            List<Spawn.Config<?>> spawns = new ArrayList<>();
            for (Node spawnNode : node.children()) {
                spawns.add(this.spawnParser.parse(spawnNode).orFail());
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.regionParser = context.of(RegionParser.Union.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("multi-region spawn");
        }

        @Override
        protected Result<RegionSpawnVector.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            AbstractRegion.Config region = this.regionParser.parseWithDefinition(node, name, value).orFail();
            float yaw = this.parseYaw(node);
            float pitch = this.parsePitch(node);

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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.regionParser = context.type(AbstractRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("single region spawn");
        }

        @Override
        protected Result<RegionSpawnVector.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            AbstractRegion.Config<?> region = this.regionParser.parseWithDefinition(node, name, value).orFail();
            float yaw = this.parseYaw(node);
            float pitch = this.parsePitch(node);

            return Result.fine(node, name, value, new RegionSpawnVector.Config() {
                public String id() { return id; }
                public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
                public float yaw() { return yaw; }
                public float pitch() { return pitch; }
            });
        }
    }
}
