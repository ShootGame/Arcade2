package pl.themolka.arcade.xml;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {
    public static final String SPLIT_KEY = ",";

    public static List<Element> children(Element element, String... children) {
        List<Element> results = new ArrayList<>();
        for (String child : children) {
            results.addAll(element.getChildren(child));
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

    public static Map<String, Attribute> parseAttributeMap(Element xml) {
        Map<String, Attribute> data = new HashMap<>();
        for (Attribute attribute : xml.getAttributes()) {
            data.put(attribute.getName(), attribute);
        }

        return data;
    }

    //
    // Deprecated parsing methods - will be removed in the near future
    //

    @Deprecated
    public static boolean parseBoolean(String input) {
        return parseBoolean(input, false);
    }

    @Deprecated
    public static boolean parseBoolean(String input, boolean def) {
        try {
            return Parsers.booleanParser().parse(field(input));
        } catch (ParserException ex) {
            return def;
        }
    }

    @Deprecated
    public static double parseDouble(String input) {
        return parseDouble(input, 0.0D);
    }

    @Deprecated
    public static double parseDouble(String input, double def) {
        try {
            return Parsers.doubleParser().parse(field(input));
        } catch (ParserException ex) {
            return def;
        }
    }

    @Deprecated
    public static String parseEnumValue(String key) {
        key = normalizeInput(key);
        if (key != null) {
            return key.toUpperCase().trim().replace(" ", "_").replace("-", "_");
        }

        return null;
    }

    @Deprecated
    public static float parseFloat(String input) {
        return parseFloat(input, 0.0F);
    }

    @Deprecated
    public static float parseFloat(String input, float def) {
        try {
            return Parsers.floatParser().parse(field(input));
        } catch (ParserException ex) {
            return def;
        }
    }

    @Deprecated
    public static int parseInt(String input) {
        return parseInt(input, 0);
    }

    @Deprecated
    public static int parseInt(String input, int def) {
        try {
            return Parsers.intParser().parse(field(input));
        } catch (ParserException ex) {
            return def;
        }
    }

    @Deprecated
    public static long parseLong(String input) {
        return parseLong(input, 0L);
    }

    @Deprecated
    public static long parseLong(String input, long def) {
        try {
            return Parsers.longParser().parse(field(input));
        } catch (ParserException ex) {
            return def;
        }
    }

    @Deprecated
    public static String parseMessage(String message) {
        try {
            return Parsers.messageParser().parse(field(message));
        } catch (ParserException ex) {
            return null;
        }
    }

    //
    // Helper Methods
    //

    /*
     * Used for legacy code
     */
    private static pl.themolka.arcade.dom.Element field(String input) {
        return Property.of("legacy", input);
    }

    private static String normalizeInput(String input) {
        if (input != null) {
            input = input.trim();

            if (!input.isEmpty()) {
                return input;
            }
        }

        return null;
    }
}
