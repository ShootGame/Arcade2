package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Produces(FilterSet.Config.class)
public class FilterSetParser extends ConfigParser<FilterSet.Config>
                             implements InstallableParser {
    private Parser<Filter.Config> filterParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("filter set");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Filter.Config.class);
    }

    @Override
    protected Result<FilterSet.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseRequiredId(node);

        Set<Filter.Config<?>> filters = new HashSet<>();
        for (Node filterNode : node.children()) {
            filters.add(this.filterParser.parse(filterNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(filters)) {
            throw this.fail(node, name, null, "No filters defined");
        }

        return Result.fine(node, name, new FilterSet.Config() {
            public String id() { return id; }
            public Ref<Set<Filter.Config<?>>> filters() { return Ref.ofProvided(filters); }
        });
    }
}
