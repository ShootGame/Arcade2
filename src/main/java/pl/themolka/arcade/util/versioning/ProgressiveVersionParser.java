package pl.themolka.arcade.util.versioning;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(ProgressiveVersion.class)
public class ProgressiveVersionParser extends ElementParser<ProgressiveVersion>
                                      implements InstallableParser {
    private Parser<Integer> valueParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.valueParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("progressive version (integer)");
    }

    @Override
    protected Result<ProgressiveVersion> parseElement(Element element, String name, String value) throws ParserException {
        int integer = this.valueParser.parseWithDefinition(element, name, value).orFail();
        if (integer < 0) {
            throw this.fail(element, name, value, "Progressive version value cannot be negative");
        }

        return Result.fine(element, name, value, new ProgressiveVersion(integer));
    }
}
