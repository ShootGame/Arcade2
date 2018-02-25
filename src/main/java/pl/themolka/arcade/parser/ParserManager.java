package pl.themolka.arcade.parser;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.util.PluginInstallable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParserManager {
    private final ArcadePlugin plugin;

    private final ParserContainer container = new ParserContainer();
    private final Map<Class<?>, Class<? extends Parser>> byType = new HashMap<>();
    private boolean installed = false;

    public ParserManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public ParserContext createContext() {
        return new ParserContext(this);
    }

    public <T> Parser<T> forType(Class<T> type) {
        Class<? extends Parser> clazz = this.forTypeClass(type);
        return clazz != null ? this.container.getParser(clazz) : null;
    }

    public Class<? extends Parser> forTypeClass(Class<?> type) {
        return this.byType.get(type);
    }

    public ParserContainer getContainer() {
        return this.container;
    }

    public Set<Class<?>> getTypes() {
        return this.byType.keySet();
    }

    public void install() {
        if (this.installed) {
            throw new IllegalStateException("Already installed!");
        }

        ParserContext context = this.createContext();
        for (Parser<?> parser : this.container.getParsers()) {
            if (parser instanceof PluginInstallable) {
                ((PluginInstallable) parser).installPlugin(this.plugin);
            }
            if (parser instanceof InstallableParser) {
                ((InstallableParser) parser).install(context);
            }
        }

        this.installed = true;
    }

    public void registerType(Class<?> type, Parser<?> parser) {
        this.registerType(type, parser.getClass());
    }

    public void registerType(Class<?> type, Class<? extends Parser> parser) {
        this.byType.put(type, parser);
    }

    public boolean unregisterType(Class<?> type) {
        return this.byType.remove(type) != null;
    }
}
