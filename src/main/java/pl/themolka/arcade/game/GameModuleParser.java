package pl.themolka.arcade.game;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.NodeParser;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for all {@link GameModule} parsers.
 */
public abstract class GameModuleParser<M extends GameModule,
                                       T extends IGameModuleConfig<M>> extends NodeParser<T> {
    private final Class<M> moduleClass;

    public GameModuleParser(Class<M> moduleClass) {
        this.moduleClass = Objects.requireNonNull(moduleClass, "moduleClass cannot be null");
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.getModuleClass().getSimpleName() + " module");
    }

    public abstract Node define(Node source);

    public Class<M> getModuleClass() {
        return this.moduleClass;
    }
}
