package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

public enum KitContentType implements KitContentParser<KitContent<?>> {
    BOOTS("boots") {
        @Override
        public BootsContent parse(Element xml) throws DataConversionException {
            return BOOTS_PARSER.parse(xml);
        }
    },

    CHESTPLATE("chestplate") {
        @Override
        public ChestplateContent parse(Element xml) throws DataConversionException {
            return CHESTPLATE_PARSER.parse(xml);
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

    FLY_SPEED("fly-speed", "flyspeed") {
        @Override
        public FlySpeedContent parse(Element xml) throws DataConversionException {
            return FLY_SPEED_PARSER.parse(xml);
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

    HELD_SLOT("held-slot", "heldslot", "held-item", "helditem", "held", "slot") {
        @Override
        public HeldSlotContent parse(Element xml) throws DataConversionException {
            return HELD_SLOT_PARSER.parse(xml);
        }
    },

    HELMET("helmet") {
        @Override
        public HelmetContent parse(Element xml) throws DataConversionException {
            return HELMET_PARSER.parse(xml);
        }
    },

    ITEM_STACK("item", "itemstack", "item-stack", "stack") {
        @Override
        public ItemStackContent parse(Element xml) throws DataConversionException {
            return ITEM_STACK_PARSER.parse(xml);
        }
    },

    KILL("kill") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return KILL_PARSER.parse(xml);
        }
    },

    KNOCKBACK("knockback", "knockback-reduction") {
        @Override
        public KnockbackContent parse(Element xml) throws DataConversionException {
            return KNOCKBACK_PARSER.parse(xml);
        }
    },

    LEGGINGS("leggings") {
        @Override
        public LeggingsContent parse(Element xml) throws DataConversionException {
            return LEGGINGS_PARSER.parse(xml);
        }
    },

    LIVES("lives", "life") {
        @Override
        public KitContent<?> parse(Element xml) throws DataConversionException {
            return LIVES_PARSER.parse(xml);
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

    SATURATION("saturation") {
        @Override
        public SaturationContent parse(Element xml) throws DataConversionException {
            return SATURATION_PARSER.parse(xml);
        }
    },

    SOUND("sound", "play-sound", "playsound") {
        @Override
        public SoundContent parse(Element xml) throws DataConversionException {
            return SOUND_PARSER.parse(xml);
        }
    },

    WALK_SPEED("walk-speed", "walkspeed", "walk") {
        @Override
        public WalkSpeedContent parse(Element xml) throws DataConversionException {
            return WALK_SPEED_PARSER.parse(xml);
        }
    },
    ;

    public static final BootsContent.LegacyParser BOOTS_PARSER = new BootsContent.LegacyParser();
    public static final ChestplateContent.LegacyParser CHESTPLATE_PARSER = new ChestplateContent.LegacyParser();
    public static final ClearInventoryContent.LegacyParser CLEAR_INVENTORY_PARSER = new ClearInventoryContent.LegacyParser();
    public static final EffectContent.LegacyParser EFFECT_PARSER = new EffectContent.LegacyParser();
    public static final FlyContent.LegacyParser FLY_PARSER = new FlyContent.LegacyParser();
    public static final FlySpeedContent.LegacyParser FLY_SPEED_PARSER = new FlySpeedContent.LegacyParser();
    public static final FoodLevelContent.LegacyParser FOOD_LEVEL_PARSER = new FoodLevelContent.LegacyParser();
    public static final GameModeContent.LegacyParser GAME_MODE_PARSER = new GameModeContent.LegacyParser();
    public static final HealthContent.LegacyParser HEALTH_PARSER = new HealthContent.LegacyParser();
    public static final HeldSlotContent.LegacyParser HELD_SLOT_PARSER = new HeldSlotContent.LegacyParser();
    public static final HelmetContent.LegacyParser HELMET_PARSER = new HelmetContent.LegacyParser();
    public static final ItemStackContent.LegacyParser ITEM_STACK_PARSER = new ItemStackContent.LegacyParser();
    public static final KillContent.LegacyParser KILL_PARSER = new KillContent.LegacyParser();
    public static final KnockbackContent.LegacyParser KNOCKBACK_PARSER = new KnockbackContent.LegacyParser();
    public static final LeggingsContent.LegacyParser LEGGINGS_PARSER = new LeggingsContent.LegacyParser();
    public static final LivesContent.LegacyParser LIVES_PARSER = new LivesContent.LegacyParser();
    public static final MaxHealthContent.LegacyParser MAX_HEALTH_PARSER = new MaxHealthContent.LegacyParser();
    public static final MessageContent.LegacyParser MESSAGE_PARSER = new MessageContent.LegacyParser();
    public static final SaturationContent.LegacyParser SATURATION_PARSER = new SaturationContent.LegacyParser();
    public static final SoundContent.LegacyParser SOUND_PARSER = new SoundContent.LegacyParser();
    public static final WalkSpeedContent.LegacyParser WALK_SPEED_PARSER = new WalkSpeedContent.LegacyParser();

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

    public static KitContent<?> parseForName(String name, Element xml) throws DataConversionException {
        KitContentType parser = forName(name);
        if (parser != null) {
            return parser.parse(xml);
        }

        return null;
    }
}
