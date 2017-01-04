package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

public enum KitContentType implements KitContentParser<Object> {
    ARMOR("armor") {
        @Override
        public ArmorContent parse(Element xml) throws DataConversionException {
            return ARMOR_PARSER.parse(xml);
        }
    },

    CLEAR_INVENTORY("clear-inventory", "clearinventory", "clear") {
        @Override
        public ClearInventoryContent parse(Element xml) throws DataConversionException {
            return CLEAR_INVENTORY_PARSER.parse(xml);
        }
    },

    EFFECT("effect", "potioneffect", "potion-effect") {
        @Override
        public EffectContent parse(Element xml) throws DataConversionException {
            return EFFECT_PARSER.parse(xml);
        }
    },

    FLY("fly", "flying") {
        @Override
        public FlyContent parse(Element xml) throws DataConversionException {
            return FLY_PARSER.parse(xml);
        }
    },

    FOOD_LEVEL("food-level", "foodlevel", "food") {
        @Override
        public FoodLevelContent parse(Element xml) throws DataConversionException {
            return FOOD_LEVEL_PARSER.parse(xml);
        }
    },

    GAME_MODE("gamemode", "game-mode", "mode") {
        @Override
        public GameModeContent parse(Element xml) throws DataConversionException {
            return GAME_MODE_PARSER.parse(xml);
        }
    },

    HEALTH("health") {
        @Override
        public HealthContent parse(Element xml) throws DataConversionException {
            return HEALTH_PARSER.parse(xml);
        }
    },

    ITEM_STACK("item", "itemstack", "item-stack", "stack") {
        @Override
        public ItemStackContent parse(Element xml) throws DataConversionException {
            return ITEM_STACK_PARSER.parse(xml);
        }
    },

    KNOCKBACK("knockback", "knockback-reduction") {
        @Override
        public KnockbackContent parse(Element xml) throws DataConversionException {
            return KNOCKBACK_PARSER.parse(xml);
        }
    },

    MAX_HEALTH("max-health", "maxhealth") {
        @Override
        public MaxHealthContent parse(Element xml) throws DataConversionException {
            return MAX_HEALTH_PARSER.parse(xml);
        }
    },

    MESSAGE("message", "msg") {
        @Override
        public MessageContent parse(Element xml) throws DataConversionException {
            return MESSAGE_PARSER.parse(xml);
        }
    },

    PERMISSION("permission") {
        @Override
        public PermissionContent parse(Element xml) throws DataConversionException {
            return PERMISSION_PARSER.parse(xml);
        }
    },

    SATURATION("saturation") {
        @Override
        public SaturationContent parse(Element xml) throws DataConversionException {
            return SATURATION_PARSER.parse(xml);
        }
    },

    WALK_SPEED("walk-speed", "walkspeed", "walk") {
        @Override
        public WalkSpeedContent parse(Element xml) throws DataConversionException {
            return WALK_SPEED_PARSER.parse(xml);
        }
    },
    ;

    public static final ArmorContent.Parser ARMOR_PARSER = new ArmorContent.Parser();
    public static final ClearInventoryContent.Parser CLEAR_INVENTORY_PARSER = new ClearInventoryContent.Parser();
    public static final EffectContent.Parser EFFECT_PARSER = new EffectContent.Parser();
    public static final FlyContent.Parser FLY_PARSER = new FlyContent.Parser();
    public static final FoodLevelContent.Parser FOOD_LEVEL_PARSER = new FoodLevelContent.Parser();
    public static final GameModeContent.Parser GAME_MODE_PARSER = new GameModeContent.Parser();
    public static final HealthContent.Parser HEALTH_PARSER = new HealthContent.Parser();
    public static final ItemStackContent.Parser ITEM_STACK_PARSER = new ItemStackContent.Parser();
    public static final KnockbackContent.Parser KNOCKBACK_PARSER = new KnockbackContent.Parser();
    public static final MaxHealthContent.Parser MAX_HEALTH_PARSER = new MaxHealthContent.Parser();
    public static final MessageContent.Parser MESSAGE_PARSER = new MessageContent.Parser();
    public static final PermissionContent.Parser PERMISSION_PARSER = new PermissionContent.Parser();
    public static final SaturationContent.Parser SATURATION_PARSER = new SaturationContent.Parser();
    public static final WalkSpeedContent.Parser WALK_SPEED_PARSER = new WalkSpeedContent.Parser();

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
