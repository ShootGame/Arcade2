package pl.themolka.arcade.kit;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;

public class BaseRemovableContentParser<T extends KitContent<?>> extends BaseContentParser<T>
                                                                 implements InstallableParser {
    private Parser<Boolean> resetParser;

    @Override
    public void install(ParserContext context) {
        this.resetParser = context.type(Boolean.class);
    }

    protected boolean reset(Node node) throws ParserException {
        return this.resetParser.parse(node.property("reset")).orDefault(false);
    }
}
