package pl.themolka.arcade.task;

import pl.themolka.arcade.ArcadePlugin;

public class TaskExecutor implements Comparable<TaskExecutor>, Runnable {
    public static final long TICK_SLEEP = 50L;

    private final ArcadePlugin plugin;

    private final int id;
    private final TaskListener listener;
    private long ticks, seconds, minutes, hours, days;

    public TaskExecutor(ArcadePlugin plugin, int id, TaskListener listener) {
        this.plugin = plugin;

        this.id = id;
        this.listener = listener;
    }

    @Override
    public int compareTo(TaskExecutor object) {
        return Integer.compare(this.getId(), object.getId());
    }

    @Override
    public void run() {
        // ticks
        this.getListener().onTick(this.ticks++);

        // seconds
        if (this.ticks % TICK_SLEEP == 0) {
            this.getListener().onSecond(this.seconds++);

            // minutes
            if (this.seconds % 60 == 0) {
                this.getListener().onMinute(this.minutes++);
            
                // hours
                if (this.minutes % 60 == 0) {
                    this.getListener().onHour(this.hours++);

                    // days
                    if (this.hours % 24 == 0) {
                        this.getListener().onDay(this.days++);
                    }
                }
            }
        }
    }

    public void createTask() {
        this.getListener().onCreate();
    }

    public void destroyTask() {
        this.getListener().onDestroy();
    }
    
    public int getId() {
        return this.id;
    }

    public TaskListener getListener() {
        return this.listener;
    }

    public boolean isAsync() {
        return false;
    }
}
