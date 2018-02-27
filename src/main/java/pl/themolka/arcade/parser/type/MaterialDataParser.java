package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(MaterialData.class)
public class MaterialDataParser extends ElementParser<MaterialData>
                                implements InstallableParser {
    private Parser<Byte> byteParser;
    private Parser<Material> materialParser;

    @Override
    public void install(ParserContext context) {
        this.byteParser = context.type(Byte.class);
        this.materialParser = context.type(Material.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a material data type");
    }

    @Override
    protected ParserResult<MaterialData> parseElement(Element element, String name, String value) throws ParserException {
        String[] split = value.split(":", 2);

        Material material = this.materialParser.parseWithValue(element, value).orFail();
        byte data = this.byteParser.parseWithValue(element, split.length > 1 ? split[1] : null).orDefault((byte) 0);

        return ParserResult.fine(element, name, value, new MaterialData(material, data));
    }
}
