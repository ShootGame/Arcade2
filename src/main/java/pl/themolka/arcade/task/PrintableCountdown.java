package pl.themolka.arcade.task;

import org.bukkit.boss.BossBar;

import java.time.Duration;

public class PrintableCountdown extends Countdown {
    public static final String FIELD_TIME_LEFT = "%TIME_LEFT%";
    public static final int[] SECONDS = {0, 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 45, 60};

    private BossBar boss;

    public PrintableCountdown(TaskManager tasks) {
        super(tasks);
    }

    public PrintableCountdown(TaskManager tasks, Duration duration) {
        super(tasks, duration);
    }

    public BossBar getBossBar() {
        return this.boss;
    }

    public String getPrintMessage(String message) {
        long left = this.getLeftSeconds();

        String result = Long.toString(left) + " seconds";
        if (left == 1L) {
            result = Long.toString(left) + " second";
        }

        return message.replace(FIELD_TIME_LEFT, result);
    }

    public double getProgress() {
        if (this.getDurationSeconds() > 60) {
            return 0;
        }

        return (1D / this.getDurationSeconds()) * this.getSeconds();
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

    public void setBossBar(BossBar boss) {
        this.boss = boss;
    }
}
