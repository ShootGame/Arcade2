package pl.themolka.arcade.kit.content;

import com.google.common.collect.ImmutableList;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class BaseContentParser<T extends KitContent<?>> extends NodeParser<T>
                                                                 implements InstallableParser {
    private final List<String> names;

    public BaseContentParser() {
        NestedParserName name = this.getClass().getDeclaredAnnotation(NestedParserName.class);
        if (name == null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                    " must be @" + NestedParserName.class.getSimpleName() + " decorated!");
        }

        if (name.value().length == 0) {
            throw new UnsupportedOperationException("No parser name provided.");
        }

        this.names = ImmutableList.<String>builder().add(name.value()).build();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.names.get(0) + " kit content");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
    }

    public List<String> names() {
        return this.names;
    }
}
