package pl.themolka.arcade.parser;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.parser.number.ByteParser;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.FloatParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.number.ShortParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.MaterialDataParser;
import pl.themolka.arcade.parser.type.MaterialParser;
import pl.themolka.arcade.parser.type.MessageParser;
import pl.themolka.arcade.parser.type.TextParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParserManager {
    private final ArcadePlugin plugin;

    private final ParserContainer container = new ParserContainer();
    private final Map<Class<?>, Class<? extends Parser>> byType = new HashMap<>();

    public ParserManager(ArcadePlugin plugin) {
        this.plugin = plugin;
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

    public void registerType(Class<?> type, Parser<?> parser) {
        this.registerType(type, parser.getClass());
    }

    public void registerType(Class<?> type, Class<? extends Parser> parser) {
        this.byType.put(type, parser);
    }

    public boolean unregisterType(Class<?> type) {
        return this.byType.remove(type) != null;
    }

    /**
     * @deprecated Move this to a file?
     */
    @Deprecated public void registerDefaults() {
        ParserContainer container = this.getContainer();

        // Numbers
        this.register(container, ByteParser.class, Byte.class);
        this.register(container, DoubleParser.class, Double.class);
        this.register(container, FloatParser.class, Float.class);
        this.register(container, IntegerParser.class, Integer.class);
        this.register(container, LongParser.class, Long.class);
        this.register(container, ShortParser.class, Short.class);

        // Standard
        this.register(container, BooleanParser.class, Boolean.class);
        this.register(container, MessageParser.class);
        this.register(container, TextParser.class, String.class);

        // Default
        this.register(container, MaterialDataParser.class, MaterialData.class);
        this.register(container, MaterialParser.class, Material.class);
    }

    private void register(ParserContainer container, Class<? extends Parser<?>> parser, Class<?>... types) {
        try {
            Parser<?> object = parser.newInstance();
            if (object != null && !container.contains(parser)
                    && !container.containsParser(object)) {
                container.register(object);
            }

            for (Class<?> type : types) {
                this.registerType(type, object);
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
