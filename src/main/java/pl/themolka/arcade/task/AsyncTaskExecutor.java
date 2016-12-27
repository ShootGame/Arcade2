package pl.themolka.arcade.task;

import pl.themolka.arcade.ArcadePlugin;

public class AsyncTaskExecutor extends TaskExecutor {
    private final AsyncTaskThread thread;

    public AsyncTaskExecutor(ArcadePlugin plugin, int id, TaskListener listener) {
        super(plugin, id, listener);

        this.thread = new AsyncTaskThread(plugin, this);
    }

    @Override
    public void createTask() {
        super.createTask();
        this.getThread().start();
    }

    @Override
    public void destroyTask() {
        this.getThread().interrupt();
        super.destroyTask();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public AsyncTaskThread getThread() {
        return this.thread;
    }
}
