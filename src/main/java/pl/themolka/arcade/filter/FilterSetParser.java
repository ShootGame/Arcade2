package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(FilterSet.class)
public class FilterSetParser extends ConfigParser<FilterSet.Config>
                             implements InstallableParser {
    private Parser<Filter> filterParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("filter set");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Filter.class);
    }

    @Override
    protected ParserResult<FilterSet.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseRequiredId(node);

        List<Filter> filters = new ArrayList<>();
        for (Node filterNode : node.children()) {
            filters.add(this.filterParser.parse(filterNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(filters)) {
            throw this.fail(node, name, null, "No filters defined");
        }

        return ParserResult.fine(node, name, new FilterSet.Config() {
            public String id() { return id; }
            public List<Filter> filters() { return filters; }
        });
    }
}
