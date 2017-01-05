package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Location;
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
                case "cylinder": return parseCylinder(map, xml);
                case "rectangle": return parseRectangle(map, xml);
                case "negative": return parseNegative(map, xml);
                case "sphere": return parseSphere(map, xml);
                case "union": return parseUnion(map, xml);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    public static CylinderRegion parseCylinder(ArcadeMap map, Element xml) throws NumberFormatException {
        Location center = parseLocation(map, "center", AbstractRegion.MIN_HEIGHT, xml);
        double height = Long.parseLong(xml.getAttributeValue("height"));
        double radius = Long.parseLong(xml.getAttributeValue("radius"));
        return new CylinderRegion(parseId(xml), map, center, height, radius);
    }

    public static RectangleRegion parseRectangle(ArcadeMap map, Element xml) throws NumberFormatException {
        Location min = parseLocation(map, "min", AbstractRegion.MIN_HEIGHT, xml);
        Location max = parseLocation(map, "max", AbstractRegion.MAX_HEIGHT, xml);
        return new RectangleRegion(parseId(xml), map, min, max);
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

    public static SphereRegion parseSphere(ArcadeMap map, Element xml) throws NumberFormatException {
        Location center = parseLocation(map, "center", AbstractRegion.MIN_HEIGHT, xml);
        double radius = Long.parseLong(xml.getAttributeValue("radius"));
        return new SphereRegion(parseId(xml), map, center, radius);
    }

    public static UnionRegion parseUnion(ArcadeMap map, Element xml) throws NumberFormatException {
        List<Region> regions = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Region region = parse(map, child);

            if (region != null) {
                regions.add(region);
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

    private static Location parseLocation(ArcadeMap map, String prefix, double defY, Element xml) {
        double x = 0D;
        double y = defY;
        double z = 0D;

        Attribute xAttribute = xml.getAttribute(prefix + "-x");
        if (xAttribute != null) {
            try {
                x = xAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        Attribute yAttribute = xml.getAttribute(prefix + "-y");
        if (xAttribute != null) {
            try {
                y = yAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        Attribute zAttribute = xml.getAttribute(prefix + "-z");
        if (zAttribute != null) {
            try {
                z = zAttribute.getDoubleValue();
            } catch (DataConversionException ignored) {
            }
        }

        return new Location(map.getWorld(), x, y, z);
    }
}
