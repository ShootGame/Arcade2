package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.util.Vector;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLRegion extends XMLParser {
    public static Region parse(ArcadeMap map, Element xml) {
        try {
            switch (xml.getName().toLowerCase()) {
                case "cuboid": return parseCuboid(map, xml);
                case "cylinder": return parseCylinder(map, xml);
                case "global": return parseGlobal(map);
                case "negative": return parseNegative(map, xml);
                case "point": return parsePoint(map, xml);
                case "sphere": return parseSphere(map, xml);
                case "union": return parseUnion(map, xml);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    public static CuboidRegion parseCuboid(ArcadeMap map, Element xml) throws NumberFormatException {
        Vector min = parseVector(map, "min", Region.MIN_HEIGHT, xml);
        Vector max = parseVector(map, "max", Region.MAX_HEIGHT, xml);
        return new CuboidRegion(parseId(xml), map, min, max);
    }

    public static CylinderRegion parseCylinder(ArcadeMap map, Element xml) throws NumberFormatException {
        Vector center = parseVector(map, null, Region.MIN_HEIGHT, xml);
        double radius = Double.parseDouble(xml.getAttributeValue("radius"));
        double height = Region.MAX_HEIGHT;

        String heightAttribute = xml.getAttributeValue("height");
        if (heightAttribute != null) {
            height = Double.parseDouble(heightAttribute);
        }

        return new CylinderRegion(parseId(xml), map, center, height, radius);
    }

    public static GlobalRegion parseGlobal(ArcadeMap map) {
        return new GlobalRegion(map);
    }

    public static NegativeRegion parseNegative(ArcadeMap map, Element xml) throws NumberFormatException {
        try {
            Element regionElement = xml.getChildren().get(0);
            Region region = parse(map, regionElement);

            if (region != null) {
                return new NegativeRegion(region);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return null;
    }

    public static PointRegion parsePoint(ArcadeMap map, Element xml) throws NumberFormatException {
        Vector point = parseVector(map, null, Region.MIN_HEIGHT, xml);
        return new PointRegion(parseId(xml), map, point);
    }

    public static SphereRegion parseSphere(ArcadeMap map, Element xml) throws NumberFormatException {
        Vector center = parseVector(map, null, Region.MIN_HEIGHT, xml);
        double radius = Double.parseDouble(xml.getAttributeValue("radius"));
        return new SphereRegion(parseId(xml), map, center, radius);
    }

    public static UnionRegion parseUnion(ArcadeMap map, Element xml) throws NumberFormatException {
        List<Region> regions = new ArrayList<>();
        if (xml != null) {
            for (Element child : xml.getChildren()) {
                Region region = parse(map, child);

                if (region != null) {
                    regions.add(region);
                }
            }
        }

        return new UnionRegion(map, regions.toArray(new Region[regions.size()]));
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

    private static Vector parseVector(ArcadeMap map, String prefix, double defY, Element xml) {
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
