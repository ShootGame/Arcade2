package pl.themolka.arcade.game;

import pl.themolka.arcade.config.IConfig;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.map.MapFileVersion;
import pl.themolka.arcade.parser.NodeParser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for all {@link GameModule} parsers.
 */
public abstract class GameModuleParser<M extends GameModule,
                                       T extends IConfig<M>> extends NodeParser<T> {
    private final Class<M> moduleClass;

    public GameModuleParser(Class<M> moduleClass) {
        this.moduleClass = Objects.requireNonNull(moduleClass, "moduleClass cannot be null");
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.getModuleClass().getSimpleName() + " module");
    }

    public abstract List<Node> define(MapFileVersion version, Node source);

    public Class<M> getModuleClass() {
        return this.moduleClass;
    }
}
