package pl.themolka.arcade.session;

import org.bukkit.Sound;

public enum ArcadeSound {
    CHAT_MENTION(Sound.ENTITY_CHICKEN_EGG),
    ELIMINATION(Sound.ENTITY_IRONGOLEM_DEATH),
    ENEMY_LOST(Sound.ENTITY_WITHER_DEATH),
    ENEMY_WON(Sound.ENTITY_WITHER_SPAWN),
    OBJECTIVE(Sound.ENTITY_WITHER_AMBIENT),
    OBJECTIVE_LOST(Sound.ENTITY_BLAZE_DEATH),
    OBJECTIVE_SCORED(Sound.ENTITY_FIREWORK_TWINKLE),
    STARTED(Sound.BLOCK_ANVIL_LAND),
    STARTING(Sound.ENTITY_EXPERIENCE_ORB_PICKUP),
    TICK(Sound.BLOCK_STONE_BUTTON_CLICK_ON),
    TIME_OUT(Sound.BLOCK_PORTAL_TRIGGER),
    ;

    public static final float DEFAULT_PITCH = 1.0F;
    public static final float DEFAULT_VOLUME = 1.0F;

    private final Sound sound;

    ArcadeSound(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return this.sound;
    }
}
