package pl.themolka.arcade.xml;

import org.bukkit.Material;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated {@link pl.themolka.arcade.parser.type.MaterialParser}
 */
@Deprecated
public class XMLMaterial extends XMLParser {
    public static final String ATTRIBUTE_MATERIAL = "material";

    public static Material parse(Element xml) {
        return parse(xml.getAttribute(ATTRIBUTE_MATERIAL));
    }

    public static Material parse(Element xml, Material def) {
        Material material = parse(xml);
        if (material != null) {
            return material;
        }

        return def;
    }

    public static Material parse(Attribute xml) {
        if (xml != null) {
            return Material.matchMaterial(parseEnumValue(xml.getValue().split(":")[0]));
        }

        return null;
    }

    public static Material parse(Attribute xml, Material def) {
        Material material = parse(xml);
        if (material != null) {
            return material;
        }

        return def;
    }

    public static List<Material> parseArray(Element xml) {
        return parseArray(xml, null);
    }

    public static List<Material> parseArray(Element xml, Material def) {
        return parseArray(xml.getAttribute(ATTRIBUTE_MATERIAL), def);
    }

    public static List<Material> parseArray(Attribute attribute) {
        return parseArray(attribute, null);
    }

    public static List<Material> parseArray(Attribute attribute, Material def) {
        List<Material> results = new ArrayList<>();
        if (attribute != null) {
            List<String> rawList = parseArray(attribute.getValue());
            for (String raw : rawList) {
                if (raw.isEmpty()) {
                    continue;
                }

                Material material = Material.matchMaterial(parseEnumValue(raw));
                if (material != null) {
                    results.add(material);
                }
            }
        } else if (def != null) {
            results.add(def);
        }

        return results;
    }
}
