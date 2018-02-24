package pl.themolka.arcade.parser;

import org.bukkit.Difficulty;
import org.bukkit.EntityLocation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.parser.number.ByteParser;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.parser.number.FloatParser;
import pl.themolka.arcade.parser.number.IntegerParser;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.number.ShortParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.DifficultyParser;
import pl.themolka.arcade.parser.type.GameModeParser;
import pl.themolka.arcade.parser.type.LocationParser;
import pl.themolka.arcade.parser.type.MaterialDataParser;
import pl.themolka.arcade.parser.type.MaterialParser;
import pl.themolka.arcade.parser.type.MessageParser;
import pl.themolka.arcade.parser.type.VectorParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ParserManager {
    private final ArcadePlugin plugin;

    private final ParserContainer container = new ParserContainer();
    private final Map<Class<?>, Class<? extends Parser>> byType = new HashMap<>();
    private boolean installed = false;

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

    public void install() {
        if (this.installed) {
            throw new IllegalStateException("Already installed!");
        }

        ParserContext context = new ParserContext(this);
        for (Parser<?> parser : this.container.getParsers()) {
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
        this.register(container, MessageParser.class, String.class);

        // Default
        this.register(container, DifficultyParser.class, Difficulty.class);
        this.register(container, GameModeParser.class, GameMode.class);
        this.register(container, LocationParser.class, EntityLocation.class, Location.class);
        this.register(container, MaterialDataParser.class, MaterialData.class);
        this.register(container, MaterialParser.class, Material.class);
        this.register(container, VectorParser.class, BlockVector.class, Vector.class);
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
