package pl.themolka.arcade.xml;

import org.bukkit.entity.EntityType;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLEntity extends XMLParser {
    public static final String ATTRIBUTE_ENTITY = "entity";

    public static EntityType parse(Element xml) {
        return parse(xml, null);
    }

    public static EntityType parse(Element xml, EntityType def) {
        if (xml != null) {
            return parse(xml.getAttribute(ATTRIBUTE_ENTITY), def);
        }

        return def;
    }

    public static EntityType parse(Attribute xml) {
        return parse(xml, null);
    }

    public static EntityType parse(Attribute xml, EntityType def) {
        if (xml != null) {
            return parse(xml.getValue(), def);
        }

        return def;
    }

    public static EntityType parse(String value) {
        return parse(value, null);
    }

    public static EntityType parse(String value, EntityType def) {
        if (value != null) {
            EntityType entity = EntityType.valueOf(parseEnumValue(value));
            if (entity != null) {
                return entity;
            }
        }

        return def;
    }
}
