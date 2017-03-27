package pl.themolka.arcade.time;

import org.bukkit.ChatColor;

import java.time.Duration;

public final class TimeUtils {
    private TimeUtils() {
    }

    public static String prettyDuration(Duration duration) {
        return prettyTime(Time.of(duration));
    }

    public static String prettyTime(Time time) {
        String hoursString = "";
        long hours = time.toHours();
        if (hours > 0 && hours < 10) {
            hoursString = "0" + hours + ChatColor.WHITE + ":" + ChatColor.GREEN;
        } else if (hours > 0) {
            hoursString = Long.toString(hours);
        }

        String minutesString = "00";
        long minutes = time.minusHours(hours).toMinutes();
        if (minutes > 0 && minutes < 10) {
            minutesString = "0" + minutes;
        } else if (minutes > 0) {
            minutesString = Long.toString(minutes);
        }

        String secondsString = "00";
        long seconds = time.minusHours(hours).minusMinutes(minutes).toSeconds();
        if (seconds > 0 && seconds < 10) {
            secondsString = "0" + seconds;
        } else if (seconds > 0) {
            secondsString = Long.toString(seconds);
        }

        return ChatColor.GREEN + hoursString + minutesString + ChatColor.WHITE + ":" + ChatColor.GREEN + secondsString;
    }
}
