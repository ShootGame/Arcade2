package pl.themolka.arcade.xml;

import org.bukkit.Sound;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLSound extends XMLParser {
    public static final String ATTRIBUTE_SOUND = "sound";

    public static Sound parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_SOUND));
    }

    public static Sound parse(Element xml, Sound def) {
        Sound sound = parse(xml);
        if (sound != null) {
            return sound;
        }

        return def;
    }

    public static Sound parse(Attribute xml) {
        if (xml != null) {
            return parse(xml.getValue());
        }

        return null;
    }

    public static Sound parse(Attribute xml, Sound def) {
        Sound sound = parse(xml);
        if (sound != null) {
            return sound;
        }

        return def;
    }

    public static Sound parse(String value) {
        return parse(value, null);
    }

    public static Sound parse(String value, Sound def) {
        if (value != null) {
            Sound result = Sound.valueOf(parseEnumValue(value));
            if (result != null) {
                return result;
            }
        }

        return def;
    }
}
