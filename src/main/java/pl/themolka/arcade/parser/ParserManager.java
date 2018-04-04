package pl.themolka.arcade.parser;

import pl.themolka.arcade.ArcadePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ParserManager {
    private final ArcadePlugin plugin;

    private final ParserContainer container = new ParserContainer();
    private ParserContext.Factory contextFactory;
    private final Map<Class<?>, Class<? extends Parser>> byType = new HashMap<>();
    private boolean installed = false;

    public ParserManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public ParserContext createContext() {
        return Objects.requireNonNull(this.contextFactory, "contextFactory cannot be null").createContext(this);
    }

    public <T extends Enum<T>> Parser<T> forEnumType(Class<T> type) {
        return this.createEnumParser(type);
    }

    public <T> Parser<T> forType(Class<T> type) throws ParserNotSupportedException {
        if (this.byType.containsKey(type)) {
            Class<? extends Parser> clazz = this.byType.get(type);

            Parser<T> parser = this.container.getParser(clazz);
            if (parser != null) {
                return parser;
            }
        }

        if (Enum.class.isAssignableFrom(type)) {
            return (Parser<T>) this.createEnumParser(type);
        }

        throw new ParserNotSupportedException(type.getSimpleName() + " is not supported");
    }

    public ParserContainer getContainer() {
        return this.container;
    }

    public ParserContext.Factory getContextFactory() {
        return this.contextFactory;
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

    public void setContextFactory(ParserContext.Factory contextFactory) {
        this.contextFactory = contextFactory;
    }

    public boolean supportsType(Class<?> type) {
        return this.byType.containsKey(type);
    }

    public boolean unregisterType(Class<?> type) {
        return this.byType.remove(type) != null;
    }

    // hacky method to skip generics
    private <T extends Enum<T>> EnumParser<T> createEnumParser(Class clazz) {
        return new EnumParser<>(clazz);
    }
}
