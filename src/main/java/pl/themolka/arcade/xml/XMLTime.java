package pl.themolka.arcade.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.util.Time;

public class XMLTime extends XMLParser {
    public static final String ATTRIBUTE_TIME = "time";

    public static Time parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_TIME));
    }

    public static Time parse(Element xml, Time def) {
        return parse(xml.getAttribute(ATTRIBUTE_TIME), def);
    }

    public static Time parse(Attribute xml) {
        return parse(xml, null);
    }

    public static Time parse(Attribute xml, Time def) {
        if (xml != null) {
            return Time.parseTime(xml.getValue(), def);
        }

        return def;
    }
}
