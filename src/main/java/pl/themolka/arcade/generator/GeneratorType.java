package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.xml.XMLParser;

import java.util.Properties;

public enum GeneratorType implements GeneratorCreator<Object> {
    /**
     * Custom generator provided by plugins
     */
    PLUGIN {
        @Override
        public PluginGenerator create(ArcadePlugin plugin, ArcadeMap map, Properties properties) {
            return PLUGIN_PARSER.create(plugin, map, properties);
        }
    },

    /**
     * Default game generator
     */
    VANILLA {
        @Override
        public VanillaGenerator create(ArcadePlugin plugin, ArcadeMap map, Properties properties) {
            return VANILLA_PARSER.create(plugin, map, properties);
        }
    },

    /**
     * Empty world without any blocks
     */
    VOID {
        @Override
        public VoidGenerator create(ArcadePlugin plugin, ArcadeMap map, Properties properties) {
            return VOID_PARSER.create(plugin, map, properties);
        }
    },
    ;

    public static final PluginGenerator.Parser PLUGIN_PARSER = new PluginGenerator.Parser();
    public static final VanillaGenerator.Parser VANILLA_PARSER = new VanillaGenerator.Parser();
    public static final VoidGenerator.Parser VOID_PARSER = new VoidGenerator.Parser();

    private static final VoidGenerator defaultGenerator = VOID_PARSER.create(null, null, null);

    public static GeneratorType forName(String name) {
        if (name != null) {
            String query = XMLParser.parseEnumValue(name);

            for (GeneratorType type : values()) {
                if (type.name().equals(query)) {
                    return type;
                }
            }
        }

        return null;
    }

    public static Generator getDefaultGenerator() {
        return defaultGenerator;
    }
}
