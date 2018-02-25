package pl.themolka.arcade.xml;

import org.bukkit.Location;
import org.bukkit.World;
import org.jdom2.Element;

/**
 * @deprecated {@link pl.themolka.arcade.parser.type.LocationParser}
 */
@Deprecated
public class XMLLocation extends XMLParser {
    public static final String ATTRIBUTE_X = "x";
    public static final String ATTRIBUTE_Y = "y";
    public static final String ATTRIBUTE_Z = "z";
    public static final String ATTRIBUTE_YAW = "yaw";
    public static final String ATTRIBUTE_PITCH = "pitch";

    public static final double X = 0.5;
    public static final double Y = 0.0;
    public static final double Z = 0.5;
    public static final float YAW = 0F;
    public static final float PITCH = 0F;

    public static Location parse(Element xml) {
        return parse(xml, X, Z);
    }

    public static Location parse(Element xml, double x, double z) {
        return parse(xml, x, Y, z);
    }

    public static Location parse(Element xml, double x, double y, double z) {
        return parse(xml, x, y, z, YAW, PITCH);
    }

    public static Location parse(Element xml, double x, double y, double z, float yaw, float pitch) {
        if (xml != null) {
            double paramX = parseDouble(xml.getAttributeValue(ATTRIBUTE_X), x);
            double paramY = parseDouble(xml.getAttributeValue(ATTRIBUTE_Y), y);
            double paramZ = parseDouble(xml.getAttributeValue(ATTRIBUTE_Z), z);
            float paramYaw = parseFloat(xml.getAttributeValue(ATTRIBUTE_YAW), yaw);
            float paramPitch = parseFloat(xml.getAttributeValue(ATTRIBUTE_PITCH), pitch);

            return new Location((World) null, paramX, paramY, paramZ, paramYaw, paramPitch);
        }

        return new Location((World) null, x, y, z, yaw, pitch);
    }
}
