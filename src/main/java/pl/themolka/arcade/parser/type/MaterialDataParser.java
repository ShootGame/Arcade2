package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(MaterialData.class)
public class MaterialDataParser extends ElementParser<MaterialData>
                                implements InstallableParser {
    private Parser<Material> materialParser;
    private Parser<Byte> dataParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.materialParser = context.type(Material.class);
        this.dataParser = context.type(Byte.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a material data type");
    }

    @Override
    protected ParserResult<MaterialData> parseElement(Element element, String name, String value) throws ParserException {
        String[] split = value.split(":", 2);

        Material material = this.materialParser.parseWithValue(element, split[0]).orFail();
        byte data = split.length > 1 ? this.dataParser.parseWithValue(element, split[1]).orFail() : 0;

        return ParserResult.fine(element, name, value, new MaterialData(material, data));
    }
}
