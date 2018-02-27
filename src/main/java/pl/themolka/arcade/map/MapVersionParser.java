package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(MapVersion.class)
public class MapVersionParser extends ElementParser<MapVersion> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a semantic map version (read https://semver.org/)");
    }

    @Override
    protected ParserResult<MapVersion> parseElement(Element element, String name, String value) throws ParserException {
        MapVersion version = MapVersion.valueOf(value);
        if (version != null) {
            return ParserResult.fine(element, name, value, version);
        }

        throw this.fail(element, name, value, "Illegal map version format");
    }
}
