/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.bossbar;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.Color;

public final class BossBarUtils {
    private static final BarColor def = null;

    private BossBarUtils() {
    }

    public static BarColor color(ChatColor chat) {
        return color(chat, def);
    }

    public static BarColor color(Color color) {
        return color(color, def);
    }

    public static BarColor color(DyeColor dye) {
        return color(dye, def);
    }

    public static BarColor color(Participator participator) {
        return color(participator, def);
    }

    public static BarColor color(ChatColor chat, BarColor def) {
        return chat != null ? color(Color.ofChat(chat), def) : def;
    }

    public static BarColor color(Color color, BarColor def) {
        return color != null ? color(color.toDye(), def) : def;
    }

    public static BarColor color(DyeColor dye, BarColor def) {
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
            default:
                return def;
        }
    }

    public static BarColor color(Participator participator, BarColor def) {
        return participator != null ? color(participator.getColor(), def) : def;
    }
}
