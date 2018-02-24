package pl.themolka.arcade.filter;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.MaterialData;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.damage.XMLDamage;
import pl.themolka.arcade.filter.matcher.DamageMatcher;
import pl.themolka.arcade.filter.matcher.Matcher;
import pl.themolka.arcade.filter.matcher.MaterialMatcher;
import pl.themolka.arcade.filter.matcher.VoidMatcher;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Requires rewrite.
 */
@Deprecated
public class XMLFilter extends XMLParser {
    public static Filter parse(ArcadePlugin plugin, Element xml) {
        switch (xml.getName().toLowerCase()) {
            case "abstain": return parseAbstain();
            case "all": return parseAll(plugin, xml);
            case "allow": return parseAllow();
            case "any": return parseAny(plugin, xml);
            case "damage": return parseDamage(xml);
            case "deny": return parseDeny();
            case "material": return parseMaterial(xml);
            case "none": return parseNone(plugin, xml);
            case "not": return parseNot(plugin, xml);
            case "void": return parseVoid();
            default: return parseUnknown(plugin, xml);
        }
    }

    public static StaticFilter parseAbstain() {
        return StaticFilter.ABSTAIN;
    }

    public static ModifierFilter parseAll(ArcadePlugin plugin, Element xml) {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.all(filters);
    }

    public static StaticFilter parseAllow() {
        return StaticFilter.ALLOW;
    }

    public static ModifierFilter parseAny(ArcadePlugin plugin, Element xml) {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.any(filters);
    }

    public static Filter parseDamage(Element xml) {
        EntityDamageEvent.DamageCause damage = XMLDamage.parse(xml.getValue());
        return Filters.secure(damage != null
                ? new DamageMatcher(damage)
                : null);
    }

    public static StaticFilter parseDeny() {
        return StaticFilter.DENY;
    }

    public static Filter parseMaterial(Element xml) {
        String[] split = xml.getValue().split(":");
        Material material = Material.matchMaterial(parseEnumValue(split[0]));

        byte data = MaterialMatcher.DATA_NULL;
        if (split.length > 1) {
            try {
                data = Byte.parseByte(split[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        return Filters.secure(material != null
                ? new MaterialMatcher(new MaterialData(material, data))
                : null);
    }

    public static ModifierFilter parseNone(ArcadePlugin plugin, Element xml) {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.none(filters);
    }

    public static ModifierFilter parseNot(ArcadePlugin plugin, Element xml) {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return filters.isEmpty() ? null : ModifierFilter.not(filters);
    }

    private static Filter parseUnknown(ArcadePlugin plugin, Element xml) {
        FilterParseEvent event = new FilterParseEvent(plugin, xml);
        plugin.getEventBus().publish(event);

        return event.getResult();
    }

    private static final Matcher voidMatcher = new VoidMatcher();
    private static Filter parseVoid() {
        return voidMatcher;
    }
}
