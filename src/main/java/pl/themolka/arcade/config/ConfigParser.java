package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;

public abstract class ConfigParser<T extends IConfig> extends NodeParser<T>
                                                      implements InstallableParser {
    protected Parser<String> idParser;

    @Override
    public void install(ParserContext context) {
        this.idParser = context.id();
    }

    protected String parseOptionalId(Node node) throws ParserException {
        return this.idParser.parse(this.id(node)).orDefaultNull();
    }

    protected String parseRequiredId(Node node) throws ParserException {
        return this.idParser.parse(this.id(node)).orFail();
    }

    private Property id(Node node) {
        return node.property("id");
    }
}
