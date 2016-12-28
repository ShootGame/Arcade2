package pl.themolka.arcade.session;

import org.bukkit.Sound;

public enum ArcadeSound {
    CHAT_MENTION(Sound.CHICKEN_EGG_POP),
    ELIMINATION(Sound.IRONGOLEM_DEATH),
    ENEMY_LOST(Sound.WITHER_DEATH),
    ENEMY_WON(Sound.WITHER_SPAWN),
    OBJECTIVE(Sound.WITHER_IDLE),
    OBJECTIVE_LOST(Sound.BLAZE_DEATH),
    OBJECTIVE_SCORED(Sound.FIREWORK_TWINKLE2),
    STARTED(Sound.ANVIL_LAND),
    STARTING(Sound.ORB_PICKUP),
    TICK(Sound.CLICK),
    TIME_OUT(Sound.PORTAL_TRIGGER),
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
