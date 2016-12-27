package pl.themolka.arcade.xml.parser;

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
        return DyeColor.valueOf(parseEnumValue(xml.getValue()));
    }

    public static DyeColor parse(Attribute xml, DyeColor def) {
        DyeColor color = parse(xml);
        if (color != null) {
            return color;
        }

        return def;
    }
}
