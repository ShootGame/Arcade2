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

package pl.themolka.arcade.time;

import org.bukkit.ChatColor;

import java.time.Duration;

public final class TimeUtils {
    public static final int TICKS_INT_OFFSET = Integer.MAX_VALUE;

    private TimeUtils() {
    }

    public static int toTicksInt(Time time) {
        return toTicksInt(time, TICKS_INT_OFFSET);
    }

    public static int toTicksInt(Time time, int offset) {
        long ticks = time.toTicks();
        int ticksInt = (int) ticks;
        return ticks != ticksInt ? offset : ticksInt;
    }

    public static String prettyDuration(Duration duration, String primary, String secondary) {
        return prettyTime(Time.of(duration), primary, secondary);
    }

    public static String prettyTime(Time time, String primary, String secondary) {
        long hours = time.toHours();
        String hoursString = printableString(hours, "");
        if (!hoursString.isEmpty()) {
            hoursString += ChatColor.RESET + secondary + ":" + primary;
        }

        long minutes = time.toMinutes() - Time.ZERO.plusHours(hours).toSeconds();
        String minutesString = printableString(minutes, "00");

        long seconds = time.toSeconds() - Time.ZERO.plusHours(hours).plusMinutes(minutes).toSeconds();
        String secondsString = printableString(seconds, "00");

        return primary + hoursString + minutesString + ChatColor.RESET + secondary + ":" + primary + secondsString;
    }

    private static String printableString(long time, String def) {
        return time > 0 ? (time < 10 ? "0" : "") + Long.toString(time) : def;
    }
}
