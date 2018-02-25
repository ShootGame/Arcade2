package pl.themolka.arcade.xml;

import org.bukkit.World;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * @deprecated {@link pl.themolka.arcade.parser.EnumParser}
 */
@Deprecated
public class XMLEnvironment extends XMLParser {
    public static final String ATTRIBUTE_ENVIRONMENT = "environment";

    public static World.Environment parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_ENVIRONMENT));
    }

    public static World.Environment parse(Element xml, World.Environment def) {
        World.Environment environment = parse(xml);
        if (environment != null) {
            return environment;
        }

        return def;
    }

    public static World.Environment parse(Attribute xml) {
        if (xml != null) {
            return World.Environment.valueOf(parseEnumValue(xml.getValue()));
        }

        return null;
    }

    public static World.Environment parse(Attribute xml, World.Environment def) {
        World.Environment environment = parse(xml);
        if (environment != null) {
            return environment;
        }

        return def;
    }
}
