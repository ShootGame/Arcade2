package pl.themolka.arcade.kit;

import org.jdom2.Element;

public enum KitContentParser implements IKitContentParser<Object> {
    ITEMSTACK("item", "itemstack", "stack") {
        private final ItemStackContent.Parser parser = new ItemStackContent.Parser();

        @Override
        public KitContent<Object> parse(Element xml) {
            return (KitContent) this.parser.parse(xml);
        }
    },
    ;

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

    public static KitContent<?> parseForName(String name, Element xml) {
        KitContentParser parser = forName(name);
        if (parser != null) {
            return parser.parse(xml);
        }

        return null;
    }
}
