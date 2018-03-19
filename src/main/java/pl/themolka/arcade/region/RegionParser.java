package pl.themolka.arcade.region;

import org.bukkit.util.Vector;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class RegionParser<T extends AbstractRegion.Config> extends ConfigParser<T>
                                                                    implements InstallableParser {
    private Parser<Double> xParser;
    private Parser<Double> yParser;
    private Parser<Double> zParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.xParser = context.type(Double.class);
        this.yParser = context.type(Double.class);
        this.zParser = context.type(Double.class);
    }

    protected Vector parseVector(Node node, String prefix, Double defY) throws ParserException {
        prefix = prefix != null ? prefix + "-" : "";
        double x = this.parseCoordinate(this.xParser, node.property(prefix + "x"), null);
        double y = this.parseCoordinate(this.yParser, node.property(prefix + "y"), defY);
        double z = this.parseCoordinate(this.zParser, node.property(prefix + "z"), null);
        return new Vector(x, y, z);
    }

    protected double parseCoordinate(Parser<Double> parser, Element element, Double def) throws ParserException {
        return def != null ? parser.parse(element).orDefault(def)
                           : parser.parse(element).orFail();
    }

    //
    // Auto
    //

    @Produces(AbstractRegion.Config.class)
    public static class Auto extends RegionParser
                             implements InstallableParser {
        private NestedParserMap<RegionParser<?>> nested;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.nested = new NestedParserMap<>(context);
            this.nested.scan(RegionParser.class);

            super.install(context);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("region");
        }

        @Override
        protected ParserResult<?> parseTree(Node node, String name) throws ParserException {
            RegionParser<?> parser = this.nested.parse(name);
            if (parser == null) {
                throw this.fail(node, name, null, "Unknown region type");
            }

            return parser.parseWithName(node, name);
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
        protected ParserResult<CuboidRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            Vector min = this.parseVector(node, "min", CuboidRegion.MIN_HEIGHT);
            Vector max = this.parseVector(node, "max", CuboidRegion.MAX_HEIGHT);

            return ParserResult.fine(node, name, value, new CuboidRegion.Config() {
                public String id() { return id; }
                public Vector min() { return min; }
                public Vector max() { return max; }
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.radiusParser = context.type(Double.class);
            this.heightParser = context.type(Double.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("cylindrical region");
        }

        @Override
        protected ParserResult<CylinderRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            Vector center = this.parseVector(node, null, CylinderRegion.MIN_HEIGHT);
            double radius = this.radiusParser.parse(node.property("radius", "r")).orFail();
            double height = this.heightParser.parse(node.property("height")).orDefault(CylinderRegion.MAX_HEIGHT);

            return ParserResult.fine(node, name, value, new CylinderRegion.Config() {
                public String id() { return id; }
                public Vector center() { return center; }
                public double radius() { return radius; }
                public double height() { return height; }
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
        protected ParserResult<GlobalRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);

            return ParserResult.fine(node, name, value, new GlobalRegion.Config() {
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.regionParser = context.type(UnionRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("region negation");
        }

        @Override
        protected ParserResult<NegativeRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            Ref<UnionRegion.Config> region = Ref.ofProvided(this.regionParser.parse(node).orFail());

            return ParserResult.fine(node, name, value, new NegativeRegion.Config() {
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
        protected ParserResult<PointRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            Vector point = this.parseVector(node, null, null);

            return ParserResult.fine(node, name, value, new PointRegion.Config() {
                public String id() { return id; }
                public Vector point() { return point; }
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.radiusParser = context.type(Double.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("spherical region");
        }

        @Override
        protected ParserResult<SphereRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);
            Vector center = this.parseVector(node, null, null);
            double radius = this.radiusParser.parse(node.property("radius", "r")).orFail();

            return ParserResult.fine(node, name, value, new SphereRegion.Config() {
                public String id() { return id; }
                public Vector center() { return center; }
                public double radius() { return radius; }
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
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.regionParser = context.type(AbstractRegion.Config.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("union of regions");
        }

        @Override
        protected ParserResult<UnionRegion.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseOptionalId(node);

            List<Ref<AbstractRegion.Config<AbstractRegion>>> regions = new ArrayList<>();
            for (Node member : node.children()) {
                regions.add(Ref.ofProvided(this.regionParser.parse(member).orFail()));
            }

            if (regions.isEmpty()) {
                throw this.fail(node, name, value, "No any union member regions defined");
            }

            return ParserResult.fine(node, name, value, new UnionRegion.Config() {
                public String id() { return id; }
                public List<Ref<AbstractRegion.Config<AbstractRegion>>> regions() { return regions; }
            });
        }
    }
}
