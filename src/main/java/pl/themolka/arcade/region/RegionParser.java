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

package pl.themolka.arcade.region;

import org.bukkit.util.Vector;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class RegionParser<T extends AbstractRegion.Config<?>> extends ConfigParser<T>
                                                                       implements InstallableParser {
    private Parser<Double> xParser;
    private Parser<Double> yParser;
    private Parser<Double> zParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.xParser = library.type(Double.class);
        this.yParser = library.type(Double.class);
        this.zParser = library.type(Double.class);
    }

    protected Vector parseVector(Context context, Node node, String prefix, Double defY) throws ParserException {
        prefix = prefix != null ? prefix + "-" : "";
        double x = this.parseCoordinate(this.xParser, context, node.property(prefix + "x"), null);
        double y = this.parseCoordinate(this.yParser, context, node.property(prefix + "y"), defY);
        double z = this.parseCoordinate(this.zParser, context, node.property(prefix + "z"), null);
        return new Vector(x, y, z);
    }

    protected double parseCoordinate(Parser<Double> parser, Context context, Element element, Double def) throws ParserException {
        return def != null ? parser.parse(context, element).orDefault(def)
                           : parser.parse(context, element).orFail();
    }

    //
    // Auto
    //

    @Produces(AbstractRegion.Config.class)
    public static class Auto extends RegionParser
                             implements InstallableParser {
        private NestedParserMap<RegionParser<?>> nested;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);

            this.nested = new NestedParserMap<>(library);
            this.nested.scan(RegionParser.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("region");
        }

        @Override
        protected Result<?> parseNode(Context context, Node node, String name, String value) throws ParserException {
            RegionParser<?> parser = this.nested.parse(name);
            if (parser == null) {
                throw this.fail(node, null, name, "Unknown region type");
            }

            return parser.parseWithDefinition(context, node, name, value);
        }
    }

    //
    // Cuboid
    //

    @NestedParserName("cuboid")
    @Produces(CuboidRegion.Config.class)
    public static class Cuboid extends RegionParser<CuboidRegion.Config> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("cuboid region");
        }

        @Override
        protected Result<CuboidRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            Vector min = this.parseVector(context, node, "min", CuboidRegion.MIN_HEIGHT);
            Vector max = this.parseVector(context, node, "max", CuboidRegion.MAX_HEIGHT);

            return Result.fine(node, name, value, new CuboidRegion.Config() {
                public String id() { return id; }
                public Ref<Vector> min() { return Ref.ofProvided(min); }
                public Ref<Vector> max() { return Ref.ofProvided(max); }
            });
        }
    }

    //
    // Cylinder
    //

    @NestedParserName("cylinder")
    @Produces(CylinderRegion.Config.class)
    public static class Cylinder extends RegionParser<CylinderRegion.Config>
                                 implements InstallableParser {
        private Parser<Double> radiusParser;
        private Parser<Double> heightParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.radiusParser = library.type(Double.class);
            this.heightParser = library.type(Double.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("cylindrical region");
        }

        @Override
        protected Result<CylinderRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            Vector center = this.parseVector(context, node, null, CylinderRegion.MIN_HEIGHT);
            double radius = this.radiusParser.parse(context, node.property("radius", "r")).orFail();
            double height = this.heightParser.parse(context, node.property("height")).orDefault(CylinderRegion.MAX_HEIGHT);

            return Result.fine(node, name, value, new CylinderRegion.Config() {
                public String id() { return id; }
                public Ref<Vector> center() { return Ref.ofProvided(center); }
                public Ref<Double> radius() { return Ref.ofProvided(radius); }
                public Ref<Double> height() { return Ref.ofProvided(height); }
            });
        }
    }

    //
    // Global
    //

    @NestedParserName("global")
    @Produces(GlobalRegion.Config.class)
    public static class Global extends RegionParser<GlobalRegion.Config> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("global region");
        }

        @Override
        protected Result<GlobalRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);

            return Result.fine(node, name, value, new GlobalRegion.Config() {
                public String id() { return id; }
            });
        }
    }

    //
    // Negative
    //

    @NestedParserName("negative")
    @Produces(NegativeRegion.Config.class)
    public static class Negative extends RegionParser<NegativeRegion.Config>
                                 implements InstallableParser {
        private Parser<UnionRegion.Config> regionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.regionParser = library.type(UnionRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("region negation");
        }

        @Override
        protected Result<NegativeRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            Ref<UnionRegion.Config> region = Ref.ofProvided(this.regionParser.parseWithDefinition(context, node, name, value).orFail());

            return Result.fine(node, name, value, new NegativeRegion.Config() {
                public String id() { return id; }
                public Ref<UnionRegion.Config> region() { return region; }
            });
        }
    }

    //
    // Point
    //

    @NestedParserName("point")
    @Produces(PointRegion.Config.class)
    public static class Point extends RegionParser<PointRegion.Config> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("point");
        }

        @Override
        protected Result<PointRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            Vector point = this.parseVector(context, node, null, null);

            return Result.fine(node, name, value, new PointRegion.Config() {
                public String id() { return id; }
                public Ref<Vector> point() { return Ref.ofProvided(point); }
            });
        }
    }

    //
    // Sphere
    //

    @NestedParserName("sphere")
    @Produces(SphereRegion.Config.class)
    public static class Sphere extends RegionParser<SphereRegion.Config>
                               implements InstallableParser {
        private Parser<Double> radiusParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.radiusParser = library.type(Double.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("spherical region");
        }

        @Override
        protected Result<SphereRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);
            Vector center = this.parseVector(context, node, null, null);
            double radius = this.radiusParser.parse(context, node.property("radius", "r")).orFail();

            return Result.fine(node, name, value, new SphereRegion.Config() {
                public String id() { return id; }
                public Ref<Vector> center() { return Ref.ofProvided(center); }
                public Ref<Double> radius() { return Ref.ofProvided(radius); }
            });
        }
    }

    //
    // Union
    //

    @NestedParserName("union")
    @Produces(UnionRegion.Config.class)
    public static class Union extends RegionParser<UnionRegion.Config>
                              implements InstallableParser {
        private Parser<AbstractRegion.Config> regionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.regionParser = library.type(AbstractRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("union of regions");
        }

        @Override
        protected Result<UnionRegion.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(context, node);

            Set<Ref<AbstractRegion.Config<AbstractRegion>>> regions = new LinkedHashSet<>();
            for (Node member : node.children()) {
                regions.add(Ref.ofProvided(this.regionParser.parse(context, member).orFail()));
            }

            if (regions.isEmpty()) {
                throw this.fail(node, name, value, "No union region members defined");
            }

            return Result.fine(node, name, value, new UnionRegion.Config() {
                public String id() { return id; }
                public Ref<Set<Ref<AbstractRegion.Config<AbstractRegion>>>> regions() { return Ref.ofProvided(regions); }
            });
        }
    }
}
