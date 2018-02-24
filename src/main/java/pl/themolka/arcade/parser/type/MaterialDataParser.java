package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Parsers;
import pl.themolka.arcade.parser.number.ByteParser;

import java.util.Collections;
import java.util.List;

public class MaterialDataParser extends AbstractParser<MaterialData> {
    private final ByteParser byteParser = Parsers.byteParser();
    private final MaterialParser materialParser = Parsers.materialParser();

    public MaterialDataParser() {
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
