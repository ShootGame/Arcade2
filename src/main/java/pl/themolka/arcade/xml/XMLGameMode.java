package pl.themolka.arcade.xml;

import org.bukkit.GameMode;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLGameMode extends XMLParser {
    public static final String ATTRIBUTE_GAME_MODE = "game-mode";

    public static GameMode parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_GAME_MODE));
    }

    public static GameMode parse(Element xml, GameMode def) {
        GameMode mode = parse(xml);
        if (mode != null) {
            return mode;
        }

        return def;
    }

    public static GameMode parse(Attribute xml) {
        if (xml != null) {
            return parse(parseEnumValue(xml.getValue()));
        }

        return null;
    }

    public static GameMode parse(Attribute xml, GameMode def) {
        GameMode mode = parse(xml);
        if (mode != null) {
            return mode;
        }

        return def;
    }

    public static GameMode parse(String value) {
        return parse(value, null);
    }

    public static GameMode parse(String value, GameMode def) {
        GameMode result = null;
        if (value != null) {
            try {
                result = GameMode.getByValue(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                result = GameMode.valueOf(parseEnumValue(value));
            }
        }

        if (result != null) {
            return result;
        }
        return def;
    }
}
