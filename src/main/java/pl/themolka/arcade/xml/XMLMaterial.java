package pl.themolka.arcade.xml;

import org.bukkit.Material;
import org.jdom2.Attribute;
import org.jdom2.Element;

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
            return Material.matchMaterial(parseEnumValue(xml.getValue()));
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
}
