package pl.themolka.arcade.game;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.task.Task;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class GameModule extends SimpleGameListener implements Listener, StringId {
    private ArcadePlugin plugin;

    private boolean enabled;
    private Game game;
    private boolean loaded = false;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private Module<?> module;
    private Element settings;
    private final List<Task> taskList = new CopyOnWriteArrayList<>();

    public GameModule() {
    }

    public final void initialize(ArcadePlugin plugin, Game game, Module<?> module, Element settings) {
        if (this.isLoaded()) {
            throw new IllegalStateException(this.getId() + " is already loaded.");
        }

        this.loaded = true;
        this.plugin = plugin;
        this.settings = settings;

        this.game = game;
        this.loaded = true;
        this.module = module;
    }

    @Override
    public String getId() {
        return this.getModule().getId();
    }

    public int cancelAllTasks() {
        int result = 0;
        for (Task task : this.getTaskList()) {
            if (this.cancelTask(task)) {
                result++;
            }
        }

        return result;
    }

    public boolean cancelTask(int taskId) {
        return this.plugin.getTasks().cancel(taskId);
    }

    public boolean cancelTask(Task task) {
        return this.cancelTask(task.getTaskId());
    }

    public final void destroy() {
        this.unregisterListenerObject(this);

        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
    }

    public Game getGame() {
        return this.game;
    }

    public List<Object> getListenerObjects() {
        return this.listenerObjects;
    }

    public Logger getLogger() {
        return this.getPlugin().getLogger();
    }

    public Module<?> getModule() {
        return this.module;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public Element getSettings() {
        return this.settings;
    }

    public List<Task> getTaskList() {
        return this.taskList;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void registerListenerObject(Object object) {
        if (this.listenerObjects.add(object)) {
            this.getPlugin().registerListenerObject(object);
        }
    }

    public void registerListeners() {
        if (!this.getListenerObjects().isEmpty()) {
            return;
        }

        List<Object> listeners = this.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                this.registerListenerObject(listener);
            }
        }

        this.registerListenerObject(this);
    }

    public int scheduleAsyncTask(Runnable task) {
        return this.scheduleAsyncTask(new Task(this.plugin.getTasks()) {
            @Override
            public void onTick(long ticks) {
                task.run();
            }
        });
    }

    public int scheduleAsyncTask(Task task) {
        TaskManager tasks = this.plugin.getTasks();
        int result = tasks.scheduleAsync(task);

        this.taskList.add(task);
        return result;
    }

    public int scheduleSyncTask(Runnable task) {
        return this.scheduleSyncTask(new Task(this.plugin.getTasks()) {
            @Override
            public void onTick(long ticks) {
                task.run();
            }
        });
    }

    public int scheduleSyncTask(Task task) {
        TaskManager tasks = this.plugin.getTasks();
        int result = tasks.scheduleSync(task);

        this.taskList.add(task);
        return result;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        return this.listenerObjects.remove(object);
    }
}
