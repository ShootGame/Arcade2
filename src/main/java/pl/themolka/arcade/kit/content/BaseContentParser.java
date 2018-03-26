package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.parser.BaseNestedParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public abstract class BaseContentParser<T extends KitContent.Config<? extends KitContent<?>, ?>> extends BaseNestedParser<T>
                                                                                                 implements InstallableParser {
    @Override
    protected String expectType() {
        return "kit content";
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
    }
}
