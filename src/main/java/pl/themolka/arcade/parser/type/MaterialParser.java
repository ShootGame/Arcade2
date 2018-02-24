package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;

import java.util.Collections;
import java.util.List;

public class MaterialParser extends AbstractParser<Material>
                            implements InstallableParser {
    private EnumParser<Material> enumParser;

    @Override
    public void install(ParserContext context) {
        this.enumParser = context.enumType(Material.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a material type");
    }

    @Override
    protected ParserResult<Material> parse(Element element, String name, String value) throws ParserException {
        Material material = Material.matchMaterial(value.split(":")[0]);
        if (material != null) {
            return ParserResult.fine(element, name, value, material);
        }

        return this.enumParser.parseWithDefinition(element, name, value);
    }
}
