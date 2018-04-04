package pl.themolka.arcade.filter;

import pl.themolka.arcade.parser.BaseNestedParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class BaseFilterParser<T extends Filter.Config<?>> extends BaseNestedParser<T>
                                                          implements InstallableParser {
    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
    }

    @Override
    protected String expectType() {
        return "filter";
    }
}
