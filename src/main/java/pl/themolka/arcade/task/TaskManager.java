package pl.themolka.arcade.task;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.util.Tickable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class TaskManager implements Tickable {
    private final ArcadePlugin plugin;

    private int lastUniqueId = 0;
    private final List<TaskExecutor> taskList = new CopyOnWriteArrayList<>();

    public TaskManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onTick(long tick) {
        for (TaskExecutor task : this.getTasks()) {
            try {
                task.run();
            } catch (Throwable th) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not run task executor #" + task.getId(), th);
            }
        }
    }

    public boolean cancel(int taskId) {
        if (taskId == Task.DEFAULT_TASK_ID) {
            return false;
        }

        return this.cancel(this.getTask(taskId));
    }

    public boolean cancel(Task task) {
        return this.cancel(task.getTaskId());
    }

    public boolean cancel(TaskExecutor task) {
        if (task == null) {
            return false;
        }

        task.destroyTask();
        return this.taskList.remove(task);
    }

    public int cancelAll() {
        int result = 0;
        for (TaskExecutor task : this.getTasks()) {
            if (this.cancel(task)) {
                result++;
            }
        }

        return result;
    }

    public TaskExecutor getTask(int taskId) {
        for (TaskExecutor task : this.getTasks()) {
            if (task.getId() == taskId) {
                return task;
            }
        }

        return null;
    }

    public List<TaskExecutor> getTasks() {
        return this.taskList;
    }

    public int scheduleAsync(Runnable task) {
        return this.scheduleAsync(new SimpleTaskListener() {
            @Override
            public void onTick(long ticks) {
                task.run();
            }
        });
    }

    public int scheduleAsync(TaskListener task) {
        return this.schedule(new AsyncTaskExecutor(this.plugin, this.getNextUniqueId(), task));
    }

    public int scheduleSync(Runnable task) {
        return this.scheduleSync(new SimpleTaskListener() {
            @Override
            public void onTick(long ticks) {
                task.run();
            }
        });
    }

    public int scheduleSync(TaskListener task) {
        return this.schedule(new TaskExecutor(this.plugin, this.getNextUniqueId(), task));
    }

    private synchronized int getNextUniqueId() {
        return ++this.lastUniqueId;
    }

    private synchronized int generateNextUniqueId() {
        int result = 0;
        while (this.getTask(result) != null) {
            result++;
        }

        return result + 1;
    }

    private int schedule(TaskExecutor executor) {
        executor.createTask();
        this.taskList.add(executor);

        return executor.getId();
    }
}
