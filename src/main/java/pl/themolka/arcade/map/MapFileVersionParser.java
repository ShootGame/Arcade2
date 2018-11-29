package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.versioning.SemanticVersion;

import java.util.Collections;
import java.util.Set;

@Produces(MapFileVersion.class)
public class MapFileVersionParser extends ElementParser<MapFileVersion>
                                  implements InstallableParser {
    private Parser<SemanticVersion> semanticVersionParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.semanticVersionParser = context.type(SemanticVersion.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("semantic map file version (read " + MapFileVersion.MANUAL + ")");
    }

    @Override
    protected Result<MapFileVersion> parseElement(Element element, String name, String value) throws ParserException {
        SemanticVersion semantic = this.semanticVersionParser.parseWithDefinition(element, name, value).orFail();
        return Result.fine(element, name, value, MapFileVersion.convertFrom(semantic));
    }
}
