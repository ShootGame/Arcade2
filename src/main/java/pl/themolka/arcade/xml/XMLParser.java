package pl.themolka.arcade.xml;

import org.bukkit.ChatColor;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {
    public static final char COLOR_CHAR = '`';
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

    public static boolean parseBoolean(String input) {
        return parseBoolean(input, false);
    }

    public static boolean parseBoolean(String input, boolean def) {
        input = normalizeInput(input);
        if (input != null) {
            switch (input.toLowerCase()) {
                case "true":
                case "1":
                case "+":
                case "yes":
                case "on":
                case "enable":
                case "enabled":
                    return true;
                case "false":
                case "0":
                case "-":
                case "no":
                case "off":
                case "disable":
                case "disabled":
                    return false;
            }
        }

        return def;
    }

    public static double parseDouble(String input) {
        return parseDouble(input, 0.0D);
    }

    public static double parseDouble(String input, double def) {
        input = normalizeInput(input);
        if (input != null) {
            if (unlimitedNegative(input)) {
                return Double.MIN_VALUE;
            } else if (unlimitedPositive(input)) {
                return Double.MAX_VALUE;
            }

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ignored) {
            }
        }

        return def;
    }

    public static String parseEnumValue(String key) {
        key = normalizeInput(key);
        if (key != null) {
            return key.toUpperCase().trim().replace(" ", "_").replace("-", "_");
        }

        return null;
    }

    public static float parseFloat(String input) {
        return parseFloat(input, 0.0F);
    }

    public static float parseFloat(String input, float def) {
        input = normalizeInput(input);
        if (input != null) {
            if (unlimitedNegative(input)) {
                return Float.MIN_VALUE;
            } else if (unlimitedPositive(input)) {
                return Float.MAX_VALUE;
            }

            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException ignored) {
            }
        }

        return def;
    }

    public static int parseInt(String input) {
        return parseInt(input, 0);
    }

    public static int parseInt(String input, int def) {
        input = normalizeInput(input);
        if (input != null) {
            if (unlimitedNegative(input)) {
                return Integer.MIN_VALUE;
            } else if (unlimitedPositive(input)) {
                return Integer.MAX_VALUE;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
            }
        }

        return def;
    }

    public static String parseMessage(String message) {
        message = normalizeInput(message);
        if (message != null) {
            return ChatColor.translateAlternateColorCodes(COLOR_CHAR, message) + ChatColor.RESET;
        }

        return null;
    }

    //
    // Helper Methods
    //

    private static String normalizeInput(String input) {
        if (input != null) {
            input = input.trim();

            if (!input.isEmpty()) {
                return input;
            }
        }

        return null;
    }

    private static boolean unlimitedNegative(String input) {
        return input.equalsIgnoreCase("-" + Time.FOREVER_KEY);
    }

    private static boolean unlimitedPositive(String input) {
        return input.equalsIgnoreCase(Time.FOREVER_KEY);
    }
}
