package pl.themolka.arcade.xml;

import org.bukkit.World;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

public class XMLParser {
    public static final char COLOR_CHAR = '&';

    public static Attribute getAttribute(Element xml, String name, Object def) throws DataConversionException {
        Attribute attribute = xml.getAttribute(name);
        if (attribute != null) {
            return attribute;
        }

        return new Attribute(name, def.toString());
    }

    public static String parseEnumValue(String key) {
        return key.toUpperCase().replace(" ", "_");
    }

    public static String parseMessage(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes(COLOR_CHAR, message);
    }

    public static class ChatColor {
        public static final String ATTRIBUTE_COLOR = "color";

        public static org.bukkit.ChatColor parse(Element xml) {
            return parse(xml.getAttribute(ATTRIBUTE_COLOR));
        }

        public static org.bukkit.ChatColor parse(Element xml, org.bukkit.ChatColor def) {
            org.bukkit.ChatColor color = parse(xml);
            if (color != null) {
                return color;
            }

            return def;
        }

        public static org.bukkit.ChatColor parse(Attribute xml) {
            return org.bukkit.ChatColor.valueOf(parseEnumValue(xml.getValue()));
        }

        public static org.bukkit.ChatColor parse(Attribute xml, org.bukkit.ChatColor def) {
            org.bukkit.ChatColor color = parse(xml);
            if (color != null) {
                return color;
            }

            return def;
        }
    }

    public static class ItemStack {
        public static final String ATTRIBUTE_AMOUNT = "amount";

        public static final int AMOUNT = 1;

        public static org.bukkit.inventory.ItemStack parse(Element xml) throws DataConversionException {
            org.bukkit.Material paramMaterial = Material.parse(xml);
            int paramAmount = getAttribute(xml, ATTRIBUTE_AMOUNT, AMOUNT).getIntValue();

            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(paramMaterial);
            item.setAmount(paramAmount);
            return item;
        }
    }

    public static class Location {
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

        public static org.bukkit.Location parse(Element xml) throws DataConversionException {
            return parse(xml, X, Z);
        }

        public static org.bukkit.Location parse(Element xml,
                                                double x,
                                                double z) throws DataConversionException {
            return parse(xml, x, Y, z);
        }

        public static org.bukkit.Location parse(Element xml,
                                                double x,
                                                double y,
                                                double z) throws DataConversionException {
            return parse(xml, x, y, z, YAW, PITCH);
        }

        public static org.bukkit.Location parse(Element xml,
                                                double x,
                                                double y,
                                                double z,
                                                float yaw,
                                                float pitch) throws DataConversionException {
            double paramX = getAttribute(xml, ATTRIBUTE_X, X).getDoubleValue();
            double paramY = getAttribute(xml, ATTRIBUTE_Y, Y).getDoubleValue();
            double paramZ = getAttribute(xml, ATTRIBUTE_Z, Z).getDoubleValue();
            float paramYaw = getAttribute(xml, ATTRIBUTE_YAW, YAW).getFloatValue();
            float paramPitch = getAttribute(xml, ATTRIBUTE_PITCH, PITCH).getFloatValue();

            World world = null; // TODO null
            return new org.bukkit.Location(world, paramX, paramY, paramZ, paramYaw, paramPitch);
        }
    }

    public static class Material {
        public static final String ATTRIBUTE_MATERIAL = "material";

        public static org.bukkit.Material parse(Element xml) {
            return parse(xml.getAttribute(ATTRIBUTE_MATERIAL));
        }

        public static org.bukkit.Material parse(Element xml, org.bukkit.Material def) {
            org.bukkit.Material material = parse(xml);
            if (material != null) {
                return material;
            }

            return def;
        }

        public static org.bukkit.Material parse(Attribute xml) {
            return org.bukkit.Material.matchMaterial(parseEnumValue(xml.getValue()));
        }

        public static org.bukkit.Material parse(Attribute xml, org.bukkit.Material def) {
            org.bukkit.Material material = parse(xml);
            if (material != null) {
                return material;
            }

            return def;
        }
    }
}
