package pl.themolka.arcade.xml.parser;

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
}
