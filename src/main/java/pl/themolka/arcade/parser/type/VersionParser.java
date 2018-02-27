package pl.themolka.arcade.parser.type;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.Version;

import java.util.Collections;
import java.util.List;

@Produces(Version.class)
public class VersionParser extends ElementParser<Version> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a semantic version (read https://semver.org/)");
    }

    @Override
    protected ParserResult<Version> parseElement(Element element, String name, String value) throws ParserException {
        Version version = Version.valueOf(value);
        if (version != null) {
            return ParserResult.fine(element, name, value, version);
        }

        throw this.fail(element, name, value, "Illegal version format");
    }
}
