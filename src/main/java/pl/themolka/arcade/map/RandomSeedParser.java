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

import java.util.Collections;
import java.util.Set;

@Produces(RandomSeed.class)
public class RandomSeedParser extends ElementParser<RandomSeed>
                              implements InstallableParser {
    private Parser<Long> seedParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.seedParser = context.type(Long.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("random seed");
    }

    @Override
    protected Result<RandomSeed> parseElement(Element element, String name, String value) throws ParserException {
        long seed = this.seedParser.parseWithDefinition(element, name, value).orDefault(RandomSeed.DEFAULT_SEED);
        return Result.fine(element, name, value, new RandomSeed(seed));
    }
}
