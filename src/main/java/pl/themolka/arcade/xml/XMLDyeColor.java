package pl.themolka.arcade.xml;

import org.bukkit.DyeColor;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLDyeColor extends XMLParser {
    public static final String ATTRIBUTE_COLOR = "dye-color";

    public static DyeColor parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_COLOR));
    }

    public static DyeColor parse(Element xml, DyeColor def) {
        DyeColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }

    public static DyeColor parse(Attribute xml) {
        if (xml != null) {
            return parse(xml.getValue());
        }

        return null;
    }

    public static DyeColor parse(Attribute xml, DyeColor def) {
        DyeColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }

    public static DyeColor parse(String name) {
        return parse(name, null);
    }

    public static DyeColor parse(String name, DyeColor def) {
        return DyeColor.valueOf(parseEnumValue(name));
    }
}
