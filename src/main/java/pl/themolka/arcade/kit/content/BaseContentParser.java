package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.parser.BaseNestedParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public abstract class BaseContentParser<T extends KitContent.Config<?, ?>> extends BaseNestedParser<T>
                                                                           implements InstallableParser {
    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
    }

    @Override
    protected String expectType() {
        return "kit content";
    }
}
