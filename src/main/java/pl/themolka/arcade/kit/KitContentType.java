package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

public enum KitContentType implements KitContentParser<Object> {
    GAME_MODE("gamemode", "game-mode", "mode") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return GAME_MODE_PARSER.parse(xml);
        }
    },

    ITEM_STACK("item", "itemstack", "item-stack", "stack") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return ITEM_STACK_PARSER.parse(xml);
        }
    },

    POTION_EFFECT("potion", "potioneffect", "potion-effect") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return POTION_EFFECT_PARSER.parse(xml);
        }
    }
    ;

    public static final GameModeContent.Parser GAME_MODE_PARSER = new GameModeContent.Parser();
    public static final ItemStackContent.Parser ITEM_STACK_PARSER = new ItemStackContent.Parser();
    public static final PotionEffectContent.Parser POTION_EFFECT_PARSER = new PotionEffectContent.Parser();

    private final String[] name;

    KitContentType(String... name) {
        this.name = name;
    }

    public String[] getName() {
        return this.name;
    }

    public static KitContentType forName(String name) {
        if (name != null) {
            for (KitContentType value : values()) {
                for (String valueName : value.getName()) {
                    if (valueName.equalsIgnoreCase(name)) {
                        return value;
                    }
                }
            }
        }

        return null;
    }

    public static Object parseForName(String name, Element xml) throws DataConversionException {
        KitContentType parser = forName(name);
        if (parser != null) {
            return parser.parse(xml);
        }

        return null;
    }
}
