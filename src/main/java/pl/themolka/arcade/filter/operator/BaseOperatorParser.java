package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.BaseFilterParser;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class BaseOperatorParser<T extends Operator.Config<?>> extends BaseFilterParser<T>
                                                              implements InstallableParser {
    private Parser<Filter.Config> filterParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.filterParser = context.type(Filter.Config.class);
    }

    @Override
    protected String expectType() {
        return "filter condition operator";
    }

    protected Set<Filter.Config<?>> parseBody(Node node, String name, String value) throws ParserException {
        Set<Filter.Config<?>> body = new LinkedHashSet<>();
        for (Node bodyNode : node.children()) {
            body.add(this.filterParser.parse(bodyNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(body)) {
            throw this.fail(node, name, value, "No body defined");
        }

        return body;
    }
}
