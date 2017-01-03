package pl.themolka.arcade.xml;

import org.bukkit.Difficulty;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLDifficulty extends XMLParser {
    public static final String ATTRIBUTE_DIFFICULTY = "difficulty";

    public static Difficulty parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_DIFFICULTY));
    }

    public static Difficulty parse(Element xml, Difficulty def) {
        Difficulty difficulty = parse(xml);
        if (difficulty != null) {
            return difficulty;
        }

        return def;
    }

    public static Difficulty parse(Attribute xml) {
        if (xml != null) {
            return parse(xml.getValue());
        }

        return null;
    }

    public static Difficulty parse(Attribute xml, Difficulty def) {
        Difficulty difficulty = parse(xml);
        if (difficulty != null) {
            return difficulty;
        }

        return def;
    }

    public static Difficulty parse(String value) {
        return parse(value, null);
    }

    public static Difficulty parse(String value, Difficulty def) {
        Difficulty result = null;
        if (value != null) {
            try {
                result = Difficulty.getByValue(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                result = Difficulty.valueOf(parseEnumValue(value));
            }
        }

        if (result != null) {
            return result;
        }
        return def;
    }
}
