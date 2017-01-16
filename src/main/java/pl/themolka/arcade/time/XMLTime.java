package pl.themolka.arcade.time;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.xml.XMLParser;

public class XMLTime extends XMLParser {
    public static final String ATTRIBUTE_TIME = "time";

    public static Time parse(Element xml) {
        return parse(xml, null);
    }

    public static Time parse(Element xml, Time def) {
        if (xml != null) {
            return parse(xml.getAttribute(ATTRIBUTE_TIME), def);
        }

        return def;
    }

    public static Time parse(Attribute xml) {
        return parse(xml, null);
    }

    public static Time parse(Attribute xml, Time def) {
        if (xml != null) {
            return parse(xml.getValue(), def);
        }

        return def;
    }

    public static Time parse(String value) {
        return parse(value, null);
    }

    public static Time parse(String value, Time def) {
        return Time.parseTime(value, def);
    }
}
