package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.util.Vector;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLRegion extends XMLParser {
    public static Region parse(Game game, Element xml) {
        try {
            switch (xml.getName().toLowerCase()) {
                case "cuboid": return parseCuboid(game, xml);
                case "cylinder": return parseCylinder(game, xml);
                case "global": return parseGlobal(game);
                case "negative": return parseNegative(game, xml);
                case "point": return parsePoint(game, xml);
                case "sphere": return parseSphere(game, xml);
                case "union": return parseUnion(game, xml);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    public static CuboidRegion parseCuboid(Game game, Element xml) throws NumberFormatException {
        Vector min = parseVector(game, "min", Region.MIN_HEIGHT, xml);
        Vector max = parseVector(game, "max", Region.MAX_HEIGHT, xml);
        return new CuboidRegion(game, parseId(xml), min, max);
    }

    public static CylinderRegion parseCylinder(Game game, Element xml) throws NumberFormatException {
        Vector center = parseVector(game, null, Region.MIN_HEIGHT, xml);
        double radius = Double.parseDouble(xml.getAttributeValue("radius"));
        double height = Region.MAX_HEIGHT;

        String heightAttribute = xml.getAttributeValue("height");
        if (heightAttribute != null) {
            height = Double.parseDouble(heightAttribute);
        }

        return new CylinderRegion(game, parseId(xml), center, height, radius);
    }

    public static GlobalRegion parseGlobal(Game game) {
        return new GlobalRegion(game);
    }

    public static NegativeRegion parseNegative(Game game, Element xml) throws NumberFormatException {
        try {
            Element regionElement = xml.getChildren().get(0);
            Region region = parse(game, regionElement);

            if (region != null) {
                return new NegativeRegion(region);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return null;
    }

    public static PointRegion parsePoint(Game game, Element xml) throws NumberFormatException {
        Vector point = parseVector(game, null, Region.MIN_HEIGHT, xml);
        return new PointRegion(game, parseId(xml), point);
    }

    public static SphereRegion parseSphere(Game game, Element xml) throws NumberFormatException {
        Vector center = parseVector(game, null, Region.MIN_HEIGHT, xml);
        double radius = Double.parseDouble(xml.getAttributeValue("radius"));
        return new SphereRegion(game, parseId(xml), center, radius);
    }

    public static UnionRegion parseUnion(Game game, Element xml) throws NumberFormatException {
        List<Region> regions = new ArrayList<>();
        if (xml != null) {
            for (Element child : xml.getChildren()) {
                Region region = parse(game, child);

                if (region != null) {
                    regions.add(region);
                }
            }
        }

        return new UnionRegion(game, regions.toArray(new Region[regions.size()]));
    }

    //
    // Utility
    //

    private static String parseId(Element xml) {
        Attribute attribute = xml.getAttribute("id");
        if (attribute != null) {
            return attribute.getValue();
        }

        return RandomStringUtils.randomAlphanumeric(5);
    }

    private static Vector parseVector(Game game, String prefix, double defY, Element xml) {
        double x = 0D;
        double y = defY;
        double z = 0D;

        Attribute xAttribute = xml.getAttribute(prefix != null ? prefix + "-x" : "x");
        if (xAttribute != null) {
            try {
                x = xAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        Attribute yAttribute = xml.getAttribute(prefix != null ? prefix + "-y" : "y");
        if (yAttribute != null) {
            try {
                y = yAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        Attribute zAttribute = xml.getAttribute(prefix != null ? prefix + "-z" : "z");
        if (zAttribute != null) {
            try {
                z = zAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        return new Vector(x, y, z);
    }
}
