package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(MapFileVersion.class)
public class MapFileVersionParser extends ElementParser<MapFileVersion> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("a semantic map file version (read " + MapFileVersion.SPEC_URL + ")");
    }

    @Override
    protected ParserResult<MapFileVersion> parseElement(Element element, String name, String value) throws ParserException {
        MapFileVersion version = MapFileVersion.valueOf(value);
        if (version != null) {
            return ParserResult.fine(element, name, value, version);
        }

        throw this.fail(element, name, value, "Illegal map manifest file version format");
    }
}
