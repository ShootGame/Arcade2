package pl.themolka.arcade.gamerule;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * See http://minecraft.gamepedia.com/Commands#gamerule
 */
public enum GameRuleType {
    /**
     * Whether command blocks should notify admins when they perform commands.
     */
    COMMAND_BLOCK_OUTPUT("commandBlockOutput", true),

    /**
     * Whether the server should skip checking player speed when the player is wearing elytra.
     */
    DISABLE_ELYTRA_MOVEMENT_CHECK("disableElytraMovementCheck", false, true),

    /**
     * Whether the day-night cycle and moon phases progress.
     */
    DO_DAYLIGHT_CYCLE("doDaylightCycle", true, true),

    /**
     * Whether entities that are not mobs should have drops.
     */
    DO_ENTITY_DROPS("doEntityDrops", true, true),

    /**
     * Whether fire should spread and naturally extinguish.
     */
    DO_FIRE_TICK("doFireTick", true, true),

    /**
     * Whether mobs should drop items.
     */
    DO_MOB_LOOT("doMobLoot", true, true),

    /**
     * Whether mobs should naturally spawn.
     */
    DO_MOB_SPAWNING("doMobSpawning", true, true),

    /**
     * Whether blocks should have drops.
     */
    DO_TILE_DROPS("doTileDrops", true, true),

    /**
     * Whether the weather will change.
     */
    DO_WEATHER_CYCLE("doWeatherCycle", true, true),

    /**
     * Whether the player should keep items in their inventory after death.
     */
    KEEP_INVENTORY("keepInventory", false, true),

    /**
     * Whether to log admin commands to server log.
     */
    LOG_ADMIN_COMMANDS("logAdminCommands", true),

    /**
     * The maximum number of other pushable entities a mob or player can push, before
     * taking 3 hearts suffocation damage per half-second. Setting to 0 disables the
     * rule. Damage affects survival-mode or adventure-mode players, and all mobs but
     * bats. Pushable entities include non-spectator-mode players, any mobexcept bats,
     * as well as boats and minecarts.
     */
    MAX_ENTITY_CRAMMING("maxEntityCramming", 24, true),

    /**
     * Whether creepers, zombies, endermen, ghasts, withers, ender dragons, rabbits,
     * sheep, and villagers should be able to change blocks and whether villagers,
     * zombies, skeletons, and zombie pigmen can pick up items.
     */
    MOB_GRIEFING("mobGriefing", true, true),

    /**
     * Whether the player can regenerate health naturally if their hunger is full
     * enough (doesn't affect external healing, such as golden apples, the Regeneration
     * effect, etc.).
     */
    NATURAL_REGENERATION("naturalRegeneration", true, true),

    /**
     * How often a random block tick occurs (such as plant growth, leaf decay, etc.)
     * per chunk section per game tick. 0 will disable random ticks, higher numbers
     * will increase random ticks.
     */
    RANDOM_TICK_SPEED("randomTickSpeed", 3, true),

    /**
     * Whether the debug screen shows all or reduced information; and whether the effects
     * of <code>F3+B</code> (entity hitboxes) and <code>F3+G</code> (chunk boundaries) are shown.
     */
    REDUCED_DEBUG_INFO("reducedDebugInfo", false),

    /**
     * Whether the feedback from commands executed by a player should show up in chat.
     * Also affects the default behavior of whether command blocks store their output text.
     */
    SEND_COMMAND_FEEDBACK("sendCommandFeedback", true),

    /**
     * Whether a message appears in chat when a player dies.
     */
    SHOW_DEBUG_MESSAGES("showDeathMessages", true, true),

    /**
     * The number of blocks outward from the world spawn coordinates that a player will
     * spawn in when first joining a server or when dying without a spawnpoint.
     */
    SPAWN_RADIUS("spawnRadius", 10),

    /**
     * Whether players in spectator mode can generate chunks.
     */
    SPECTATORS_GENERATE_CHUNKS("spectatorsGenerateChunks", true),
    ;

    private static final Map<String, GameRuleType> byKey = new LinkedHashMap<>();

    static {
        for (GameRuleType value : values()) {
            if (value.key != null) {
                byKey.putIfAbsent(value.key, value);
            }
        }
    }

    private final String key;
    private final Object defaultValue;
    private final boolean applicable;

    GameRuleType(String key) {
        this(key, null);
    }

    GameRuleType(String key, Object defaultValue) {
        this(key, defaultValue, false);
    }

    GameRuleType(String key, Object defaultValue, boolean applicable) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.applicable = applicable;
    }

    public String getKey() {
        return this.key;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isApplicable() {
        return this.applicable;
    }

    @Override
    public String toString() {
        return this.getKey();
    }

    public static GameRuleType byKey(String key) {
        return byKey.get(key);
    }
}
