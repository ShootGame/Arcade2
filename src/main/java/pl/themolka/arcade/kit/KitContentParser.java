package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

public enum KitContentParser implements IKitContentParser<Object> {
    ITEMSTACK("item", "itemstack", "item-stack", "stack") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return ITEMSTACK_PARSER.parse(xml);
        }
    },

    POTION_EFFECT("potion", "potioneffect", "potion-effect") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return POTION_EFFECT_PARSER.parse(xml);
        }
    }
    ;

    public static final ItemStackContent.Parser ITEMSTACK_PARSER = new ItemStackContent.Parser();
    public static final PotionEffectContent.Parser POTION_EFFECT_PARSER = new PotionEffectContent.Parser();

    private final String[] name;

    KitContentParser(String... name) {
        this.name = name;
    }

    public String[] getName() {
        return this.name;
    }

    public static KitContentParser forName(String name) {
        if (name != null) {
            for (KitContentParser value : values()) {
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
        KitContentParser parser = forName(name);
        if (parser != null) {
            return parser.parse(xml);
        }

        return null;
    }
}
