package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Propertable;
import pl.themolka.arcade.xml.XMLParser;

public enum GeneratorType implements GeneratorCreator<Generator> {
    /**
     * Default Minecraft vanilla generator
     */
    VANILLA {
        @Override
        public VanillaGenerator create(ArcadePlugin plugin, Propertable properties) {
            return VANILLA_PARSER.create(plugin, properties);
        }
    },

    /**
     * Empty world without any blocks
     */
    VOID {
        @Override
        public VoidGenerator create(ArcadePlugin plugin, Propertable properties) {
            return VOID_PARSER.create(plugin, properties);
        }
    },
    ;

    public static final VanillaGenerator.Parser VANILLA_PARSER = new VanillaGenerator.Parser();
    public static final VoidGenerator.Parser VOID_PARSER = new VoidGenerator.Parser();

    private static final VoidGenerator defaultGenerator = VOID_PARSER.create(null, null);

    /**
     * @deprecated Used in {@link XMLGenerator} which will be removed.
     */
    @Deprecated
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
