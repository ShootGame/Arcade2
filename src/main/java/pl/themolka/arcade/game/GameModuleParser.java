package pl.themolka.arcade.game;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.parser.DefinedNode;
import pl.themolka.arcade.parser.NodeParser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all {@link GameModule} parsers.
 *
 * All subclasses <b>should</b> implement the {@link #define(Node)} method which
 * will define which {@link Node} elements are legal to be parsed. The default
 * behaviour is that the defined {@link Node} name is equal to the
 * {@link Module}'s {@link Module#getId()} method.
 */
public abstract class GameModuleParser<T extends GameModule> extends NodeParser<T>
                                                             implements DefinedNode {
    private final Module<T> module;
    private final Class<T> type;

    public GameModuleParser(Module<T> module, Class<T> type) {
        this.module = Objects.requireNonNull(module, "module cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList(this.type.getSimpleName());
    }

    @Override
    public List<Node> define(Node source) {
        return source.children(this.module.getId());
    }

    public Module<T> getModule() {
        return this.module;
    }

    public Class<T> getType() {
        return this.type;
    }
}
