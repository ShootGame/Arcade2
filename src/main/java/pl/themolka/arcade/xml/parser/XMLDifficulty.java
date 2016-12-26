package pl.themolka.arcade.xml.parser;

import org.bukkit.Difficulty;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
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
        try {
            return Difficulty.getByValue(xml.getIntValue());
        } catch (DataConversionException ex) {
            return Difficulty.valueOf(parseEnumValue(xml.getValue()));
        }
    }

    public static Difficulty parse(Attribute xml, Difficulty def) {
        Difficulty difficulty = parse(xml);
        if (difficulty != null) {
            return difficulty;
        }

        return def;
    }
}
