package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.filter.BaseFilterParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class BaseMatcherParser<T extends Matcher.Config<?>> extends BaseFilterParser<T>
                                                            implements InstallableParser {
    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
    }

    @Override
    protected String expectType() {
        return "filter matcher";
    }
}
