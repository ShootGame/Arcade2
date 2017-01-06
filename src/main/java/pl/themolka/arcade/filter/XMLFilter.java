package pl.themolka.arcade.filter;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLFilter extends XMLParser {
    public static Filter parse(ArcadePlugin plugin, Element xml) {
        try {
            switch (xml.getName().toLowerCase()) {
                case "all": return parseAll(plugin, xml);
                case "allow": return parseAllow();
                case "deny": return parseDeny();
                case "material": return parseMaterial(plugin, xml);
                case "not": return parseNot(plugin, xml);
                default: return parseUnknown(plugin, xml);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    public static ModifierFilter.All parseAll(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        List<Filter> filters = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Filter filter = parse(plugin, child);
            if (filter != null) {
                filters.add(filter);
            }
        }

        if (filters.isEmpty()) {
            return null;
        }

        return new ModifierFilter.All(filters.toArray(new Filter[filters.size()]));
    }

    public static StaticFilter parseAllow() {
        return StaticFilter.ALLOW;
    }

    public static StaticFilter parseDeny() {
        return StaticFilter.DENY;
    }

    public static MaterialFilter parseMaterial(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        List<MaterialData> container = new ArrayList<>();
        for (Element item : xml.getChildren()) {
            String[] split = item.getTextNormalize().split(":");
            Material material = Material.matchMaterial(parseEnumValue(split[0]));

            byte data = MaterialFilter.DATA_NULL;
            if (split.length > 1) {
                data = Byte.parseByte(split[1]);
            }

            if (material != null) {
                container.add(new MaterialData(material, data));
            }
        }

        return new MaterialFilter(container);
    }

    public static ModifierFilter.Not parseNot(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        Filter filter = parse(plugin, xml);
        if (filter != null) {
            return new ModifierFilter.Not(filter);
        }

        return null;
    }

    private static Filter parseUnknown(ArcadePlugin plugin, Element xml) throws NumberFormatException {
        FilterParseEvent event = new FilterParseEvent(plugin, xml);
        plugin.getEventBus().publish(event);

        return event.getResult();
    }
}
