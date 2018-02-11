package pl.themolka.arcade.xml;

import org.bukkit.Location;
import org.bukkit.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

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

    public static Location parse(Element xml) throws DataConversionException {
        return parse(xml, X, Z);
    }

    public static Location parse(Element xml,
                                 double x,
                                 double z) throws DataConversionException {
        return parse(xml, x, Y, z);
    }

    public static Location parse(Element xml,
                                 double x,
                                 double y,
                                 double z) throws DataConversionException {
        return parse(xml, x, y, z, YAW, PITCH);
    }

    public static Location parse(Element xml,
                                 double x,
                                 double y,
                                 double z,
                                 float yaw,
                                 float pitch) throws DataConversionException {
        if (xml != null) {
            double paramX = getAttribute(xml, ATTRIBUTE_X, x).getDoubleValue();
            double paramY = getAttribute(xml, ATTRIBUTE_Y, y).getDoubleValue();
            double paramZ = getAttribute(xml, ATTRIBUTE_Z, z).getDoubleValue();
            float paramYaw = getAttribute(xml, ATTRIBUTE_YAW, yaw).getFloatValue();
            float paramPitch = getAttribute(xml, ATTRIBUTE_PITCH, pitch).getFloatValue();

            return new Location((World) null, paramX, paramY, paramZ, paramYaw, paramPitch);
        }

        return null;
    }
}
