package pl.themolka.arcade.filter;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Filter.class)
public class FilterParser extends ConfigParser<Filter.Config<?>>
                          implements InstallableParser {
    private static final Set<Class<?>> types = ImmutableSet.<Class<?>>builder()
            .build();

    private NestedParserMap<BaseFilterParser<?, ?>> nested;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("filter");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);

        this.nested = new NestedParserMap<>(context);
        for (Class<?> clazz : types) {
            this.nested.scan(clazz);
        }
    }

    @Override
    protected ParserResult<Filter.Config<?>> parseNode(Node node, String name, String value) throws ParserException {
        BaseFilterParser<?, ?> parser = this.nested.parse(name);
        if (parser == null) {
            throw this.fail(node, null, name, "Unknown filter type");
        }

        return ParserResult.fine(node, name, value, parser.parse(node).orFail());
    }
}
