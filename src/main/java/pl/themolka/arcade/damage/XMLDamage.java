package pl.themolka.arcade.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.xml.XMLParser;

public class XMLDamage extends XMLParser {
    public static final String ATTRIBUTE_DAMAGE = "damage";

    public static EntityDamageEvent.DamageCause parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_DAMAGE));
    }

    public static EntityDamageEvent.DamageCause parse(Element xml, EntityDamageEvent.DamageCause def) {
        EntityDamageEvent.DamageCause damage = parse(xml);
        if (damage != null) {
            return damage;
        }

        return def;
    }

    public static EntityDamageEvent.DamageCause parse(Attribute xml) {
        if (xml != null) {
            return parse(xml.getValue());
        }

        return null;
    }

    public static EntityDamageEvent.DamageCause parse(Attribute xml, EntityDamageEvent.DamageCause def) {
        EntityDamageEvent.DamageCause damage = parse(xml);
        if (damage != null) {
            return damage;
        }

        return def;
    }

    public static EntityDamageEvent.DamageCause parse(String value) {
        return parse(value, null);
    }

    public static EntityDamageEvent.DamageCause parse(String value, EntityDamageEvent.DamageCause def) {
        if (value != null) {
            EntityDamageEvent.DamageCause result = EntityDamageEvent.DamageCause.valueOf(parseEnumValue(value));
            if (result != null) {
                return result;
            }
        }

        return def;
    }
}
