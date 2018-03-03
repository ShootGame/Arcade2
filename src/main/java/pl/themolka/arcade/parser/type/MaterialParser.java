package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Material.class)
public class MaterialParser extends ElementParser<Material>
                            implements InstallableParser {
    public static final String[] MATERIAL_ELEMENT_NAMES = {"material", "type", "kind", "of"};

    private Parser<Material> materialParser;

    @Override
    public void install(ParserContext context) {
        this.materialParser = context.enumType(Material.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a material type");
    }

    @Override
    protected ParserResult<Material> parseElement(Element element, String name, String value) throws ParserException {
        Material material = Material.matchMaterial(value.split(":")[0]);
        if (material != null) {
            return ParserResult.fine(element, name, value, material);
        }

        return this.materialParser.parseWithDefinition(element, name, value);
    }
}
