package pl.themolka.arcade.xml;

import org.bukkit.ChatColor;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {
    public static final char COLOR_CHAR = '&';
    public static final String SPLIT_KEY = ",";

    public static List<String> parseArray(String value) {
        return parseArray(value, SPLIT_KEY);
    }

    public static List<String> parseArray(String value, String key) {
        List<String> results = new ArrayList<>();
        for (String split : value.split(key)) {
            String trim = split.trim();
            if (!trim.isEmpty()) {
                results.add(trim);
            }
        }

        return results;
    }

    public static Attribute getAttribute(Element xml, String name, Object def) throws DataConversionException {
        Attribute attribute = xml.getAttribute(name);
        if (attribute != null) {
            return attribute;
        }

        return new Attribute(name, def.toString());
    }

    public static Map<String, Attribute> parseAttributeMap(Element xml) {
        Map<String, Attribute> data = new HashMap<>();
        for (Attribute attribute : xml.getAttributes()) {
            data.put(attribute.getName(), attribute);
        }

        return data;
    }

    public static boolean parseBoolean(String bool) {
        return parseBoolean(bool, false);
    }

    public static boolean parseBoolean(String bool, boolean def) {
        if (bool != null) {
            return Boolean.parseBoolean(bool);
        }

        return def;
    }

    public static String parseEnumValue(String key) {
        return key.toUpperCase().replace(" ", "_").replace("-", "_");
    }

    public static String parseMessage(String message) {
        if (message != null) {
            return ChatColor.translateAlternateColorCodes(COLOR_CHAR, message);
        }

        return null;
    }
}
