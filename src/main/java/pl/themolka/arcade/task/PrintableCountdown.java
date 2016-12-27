package pl.themolka.arcade.task;

import java.time.Duration;

public class PrintableCountdown extends Countdown {
    public static final String FIELD_TIME = "%TIME%";
    public static final int[] SECONDS = {0, 1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60};

    public PrintableCountdown(TaskManager tasks) {
        super(tasks);
    }

    public PrintableCountdown(TaskManager tasks, Duration duration) {
        super(tasks, duration);
    }

    public String getPrintMessage(String message) {
        long left = this.getLeftSeconds();

        String result = Long.toString(left) + " seconds";
        if (left == 1L) {
            result = Long.toString(left) + " second";
        }

        return message.replace(FIELD_TIME, result);
    }

    public boolean isPrintable(long secondsLeft) {
        if (secondsLeft >= 60) {
            return secondsLeft % 60 == 0; // every minute
        }

        for (int second : SECONDS) {
            if (second == secondsLeft) {
                return true;
            }
        }

        return false;
    }
}
