package pl.themolka.arcade.task;

public interface TaskListener {
    void onCreate();

    void onDestroy();

    void onTick(long ticks);

    void onSecond(long seconds);

    void onMinute(long minutes);

    void onHour(long hours);

    void onDay(long days);
}
