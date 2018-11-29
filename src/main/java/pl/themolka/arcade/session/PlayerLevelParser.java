package pl.themolka.arcade.session;

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

@Produces(PlayerLevel.class)
public class PlayerLevelParser extends ElementParser<PlayerLevel>
                               implements InstallableParser {
    private Parser<Integer> levelParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.levelParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("experience level");
    }

    @Override
    protected Result<PlayerLevel> parseElement(Element element, String name, String value) throws ParserException {
        int level = this.levelParser.parseWithDefinition(element, name, value).orFail();
        if (level < 0) {
            throw this.fail(element, name, value, "Level cannot be negative (smaller than 0)");
        }

        return Result.fine(element, name, value, new PlayerLevel(level));
    }
}
