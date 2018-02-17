package pl.themolka.arcade.time;

import org.bukkit.ChatColor;

import java.time.Duration;

public final class TimeUtils {
    private TimeUtils() {
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
