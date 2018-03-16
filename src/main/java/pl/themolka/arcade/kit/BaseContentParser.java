package pl.themolka.arcade.kit;

import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.NodeParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class BaseContentParser<T extends KitContent<?>> extends NodeParser<T> {
    private final List<String> names = new ArrayList<>();

    public BaseContentParser() {
        NestedParserName name = this.getClass().getDeclaredAnnotation(NestedParserName.class);
        if (name == null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                    " must be @" + NestedParserName.class.getSimpleName() + " decorated!");
        }

        if (name.value().length == 0) {
            throw new UnsupportedOperationException("No parser name provided.");
        }

        this.names.addAll(Arrays.asList(name.value()));
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.names.get(0) + " kit content");
    }

    public List<String> names() {
        return new ArrayList<>(this.names);
    }
}
