package pl.themolka.arcade.filter;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.filter.matcher.Matcher;
import pl.themolka.arcade.filter.matcher.MaterialMatcher;
import pl.themolka.arcade.filter.matcher.VoidMatcher;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Requires rewrite.
 */
@Deprecated
public class XMLFilter extends XMLParser {
    public static Filter parse(ArcadePlugin plugin, Element xml) {
        try {
            switch (xml.getName().toLowerCase()) {
                case "abstain": return parseAbstain();
                case "all": return parseAll(plugin, xml);
                case "allow": return parseAllow();
                case "any": return parseAny(plugin, xml);
                case "deny": return parseDeny();
                case "material": return parseMaterial(xml);
                case "none": return parseNone(plugin, xml);
                case "not": return parseNot(plugin, xml);
                case "void": return parseVoid();
                default: return parseUnknown(plugin, xml);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    public static StaticFilter parseAbstain() {
        return StaticFilter.ABSTAIN;
    }

    public static ModifierFilter parseAll(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.all(filters.toArray(new Filter[filters.size()]));
    }

    public static StaticFilter parseAllow() {
        return StaticFilter.ALLOW;
    }

    public static ModifierFilter parseAny(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.any(filters.toArray(new Filter[filters.size()]));
    }

    public static StaticFilter parseDeny() {
        return StaticFilter.DENY;
    }

    public static MaterialMatcher parseMaterial(Element xml) throws NumberFormatException {
        List<MaterialData> container = new ArrayList<>();
        for (Element item : xml.getChildren()) {
            String[] split = item.getValue().split(":");
            Material material = Material.matchMaterial(parseEnumValue(split[0]));

            byte data = MaterialMatcher.DATA_NULL;
            if (split.length > 1) {
                data = Byte.parseByte(split[1]);
            }

            if (material != null) {
                container.add(new MaterialData(material, data));
            }
        }

        return new MaterialMatcher(container);
    }

    public static ModifierFilter parseNone(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);

            if (filter != null) {
                filters.add(filter);
            }
        }

        return ModifierFilter.none(filters.toArray(new Filter[filters.size()]));
    }

    public static ModifierFilter parseNot(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        Filter filter = parse(plugin, xml);
        if (filter != null) {
            return ModifierFilter.not(filter);
        }

        return null;
    }

    private static Filter parseUnknown(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        FilterParseEvent event = new FilterParseEvent(plugin, xml);
        plugin.getEventBus().publish(event);

        return event.getResult();
    }

    private static final Matcher voidMatcher = new VoidMatcher();
    private static Filter parseVoid() throws NumberFormatException {
        return voidMatcher;
    }
}
