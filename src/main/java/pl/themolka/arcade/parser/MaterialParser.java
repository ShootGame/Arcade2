package pl.themolka.arcade.parser;

import org.bukkit.Material;
import pl.themolka.arcade.dom.Element;

public class MaterialParser implements ElementParser<Material> {
    private final EnumParser<Material> enumParser = Parsers.enumParser(Material.class);

    @Override
    public Material parse(Element element, String value) throws ParserException {
        Material material = Material.matchMaterial(value);
        if (material != null) {
            return material;
        }

        return this.enumParser.parse(element, value);
    }
}
