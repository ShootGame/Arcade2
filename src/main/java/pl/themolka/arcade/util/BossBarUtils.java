package pl.themolka.arcade.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
import pl.themolka.arcade.goal.GoalHolder;

public final class BossBarUtils {
    private BossBarUtils() {
    }

    public static BarColor color(ChatColor chat) {
        return chat != null ? color(Color.ofChat(chat)) : null;
    }

    public static BarColor color(Color color) {
        return color != null ? color(color.toDye()) : null;
    }

    public static BarColor color(DyeColor dye) {
        if (dye != null) {
            switch (dye) {
                case PINK:
                    return BarColor.PINK;
                case BLUE:
                case CYAN:
                case LIGHT_BLUE:
                    return BarColor.BLUE;
                case RED:
                    return BarColor.RED;
                case GREEN:
                case LIME:
                    return BarColor.GREEN;
                case ORANGE:
                case YELLOW:
                    return BarColor.YELLOW;
                case WHITE:
                    return BarColor.WHITE;
                case MAGENTA:
                case PURPLE:
                    return BarColor.PURPLE;
            }
        }

        return null;
    }

    public static BarColor color(GoalHolder goalHolder) {
        return goalHolder != null ? color(goalHolder.getColor()) : null;
    }
}
