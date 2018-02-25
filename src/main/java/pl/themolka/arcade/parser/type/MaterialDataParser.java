package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.number.ByteParser;

import java.util.Collections;
import java.util.List;

@Produces(MaterialData.class)
public class MaterialDataParser extends AbstractParser<MaterialData>
                                implements InstallableParser {
    private ByteParser byteParser;
    private MaterialParser materialParser;

    @Override
    public void install(ParserContext context) {
        this.byteParser = context.of(ByteParser.class);
        this.materialParser = context.of(MaterialParser.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a material data type");
    }

    @Override
    protected ParserResult<MaterialData> parse(Element element, String name, String value) throws ParserException {
        String[] split = value.split(":", 2);

        Material material = this.materialParser.parseWithValue(element, value).orFail();
        byte data = this.byteParser.parseWithValue(element, split.length > 1 ? split[1] : null).orDefault((byte) 0);

        return ParserResult.fine(element, name, value, new MaterialData(material, data));
    }
}
