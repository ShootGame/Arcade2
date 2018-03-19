package pl.themolka.arcade.parser;

import pl.themolka.arcade.ArcadePlugin;

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

    public <T> Parser<T> forType(Class<T> type) throws ParserNotSupportedException {
        Class<? extends Parser> clazz = this.forTypeClass(type);
        if (clazz == null) {
            throw new ParserNotSupportedException(type.getSimpleName() + " is not supported");
        }

        Parser<T> parser = this.container.getParser(clazz);
        if (parser == null) {
            throw new ParserNotSupportedException(type.getSimpleName() + " is not supported");
        }

        return parser;
    }

    public Class<? extends Parser> forTypeClass(Class<?> type) throws ParserNotSupportedException {
        if (!this.byType.containsKey(type)) {
            throw new ParserNotSupportedException(type.getSimpleName() + " is not supported");
        }

        return this.byType.get(type);
    }

    public ParserContainer getContainer() {
        return this.container;
    }

    public Set<Class<?>> getTypes() {
        return this.byType.keySet();
    }

    public int install() throws ParserNotSupportedException {
        if (this.installed) {
            throw new IllegalStateException("Already installed!");
        }

        int done = 0;
        ParserContext context = this.createContext();
        for (Parser<?> parser : this.container.getParsers()) {
            if (parser instanceof InstallableParser) {
                ((InstallableParser) parser).install(context);
                done++;
            }
        }

        this.installed = true;
        return done;
    }

    public void registerType(Class<?> type, Parser<?> parser) {
        this.registerType(type, parser.getClass());
    }

    public void registerType(Class<?> type, Class<? extends Parser> parser) {
        this.byType.put(type, parser);
    }

    public boolean supportsType(Class<?> type) {
        return this.byType.containsKey(type);
    }

    public boolean unregisterType(Class<?> type) {
        return this.byType.remove(type) != null;
    }
}
