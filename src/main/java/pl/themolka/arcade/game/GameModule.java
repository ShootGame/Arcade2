package pl.themolka.arcade.game;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.task.Task;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.util.StringId;
import pl.themolka.arcade.util.pagination.Paginationable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class GameModule extends SimpleGameListener
                        implements GameHolder, Listener, Paginationable, StringId {
    private ArcadePlugin plugin;

    private boolean enabled;
    private Game game;
    private boolean loaded = false;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private Module<?> module;
    private final List<Task> taskList = new CopyOnWriteArrayList<>();

    public GameModule() {
    }

    public final void initialize(ArcadePlugin plugin, Game game, Module<?> module) {
        if (this.isLoaded()) {
            throw new IllegalStateException(this.getId() + " is already loaded.");
        }

        this.loaded = true;
        this.plugin = plugin;

        this.game = game;
        this.loaded = true;
        this.module = module;
    }

    @Override
    public int compareTo(Paginationable o) {
        if (o == null) {
            return 0;
        } else if (o instanceof GameModule) {
            return this.getId().compareToIgnoreCase(((GameModule) o).getId());
        }

        return this.toString().compareToIgnoreCase(o.toString());
    }

    @Override
    public String getId() {
        return this.getModule().getId();
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public String paginate(int index) {
        ChatColor color;
        if (this.isEnabled()) {
            color = ChatColor.GREEN;
        } else {
            color = ChatColor.RED;
        }

        String global = "";
        if (this.getModule().isGlobal()) {
            global = ChatColor.RED + " <global>";
        }

        Class<? extends Module<?>>[] dependency = this.getModule().getDependency();
        String dependencies = "";
        if (dependency.length > 0) {
            dependencies = ChatColor.DARK_AQUA + " depends: " + StringUtils.join(
                    dependency, ChatColor.YELLOW + ", " + ChatColor.DARK_AQUA);
        }

        return ChatColor.GRAY + "#" + index + " " + color + this.getName() + ChatColor.YELLOW +
                " (" + this.getId() + ")" + global + dependencies;
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
        this.cancelAllTasks();

        this.onDisable();

        this.unregisterListenerObject(this);
        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
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

    public String getName() {
        return this.getModule().getName();
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public List<Task> getTaskList() {
        return this.taskList;
    }

    public boolean isEnabled() {
        return this.isLoaded() && this.enabled;
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
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;

        if (wasEnabled && !enabled) {
            this.destroy();
        }
    }

    public boolean unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        return this.listenerObjects.remove(object);
    }
}
