package pl.themolka.arcade.task;

import pl.themolka.arcade.game.Game;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Countdown extends Task implements CountdownListener {
    private Duration duration;
    private Game game;
    private long seconds;

    public Countdown(TaskManager tasks) {
        this(tasks, null);
    }

    public Countdown(TaskManager tasks, Duration duration) {
        super(tasks);

        if (duration != null) {
            this.setDuration(duration);
        }
    }

    @Override
    public final void onTick(long ticks) {
        super.onTick(ticks);
    }

    @Override
    public final void onSecond(long seconds) {
        super.onSecond(seconds);

        this.seconds = seconds;
        this.onUpdate(seconds, this.getLeftSeconds());

        if (this.getLeftSeconds() == 0L) {
            this.onDone();
            this.cancelTask();
        }
    }

    @Override
    public final void onMinute(long minutes) {
        super.onMinute(minutes);
    }

    @Override
    public final void onHour(long hours) {
        super.onHour(hours);
    }

    @Override
    public final void onDay(long days) {
        super.onDay(days);
    }

    /**
     * Should be @Override
     */
    @Override
    public void onDone() {
    }

    /**
     * Should be @Override
     */
    @Override
    public void onUpdate(long seconds, long secondsLeft) {
    }

    @Override
    public final Task scheduleAsyncTask() {
        throw new UnsupportedOperationException("Countdowns must be registered in the Game object.");
    }

    @Override
    public final Task scheduleSyncTask() {
        throw new UnsupportedOperationException("Countdowns must be registered in the Game object.");
    }

    public void countAsync() {
        if (this.getGame() == null) {
            throw new UnsupportedOperationException("Countdown not registered");
        }

        this.getGame().startAsyncTask(this);
    }

    public void countSync() {
        if (this.getGame() == null) {
            throw new UnsupportedOperationException("Countdown not registered");
        }

        this.getGame().startSyncTask(this);
    }

    public Duration getDuration() {
        return this.duration;
    }

    public long getDurationSeconds() {
        return this.getDuration().get(ChronoUnit.SECONDS);
    }

    public Game getGame() {
        return this.game;
    }

    public long getLeftSeconds() {
        return this.getDurationSeconds() - this.seconds;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public boolean isDone() {
        return this.getLeftSeconds() <= 0L;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
