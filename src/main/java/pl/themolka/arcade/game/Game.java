package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.Countdown;
import pl.themolka.arcade.task.Task;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game implements Messageable, PlayerResolver {
    private final ArcadePlugin plugin;

    private final int gameId;
    private final ArcadeMap map;
    private final GameModuleContainer modules = new GameModuleContainer();
    private final GamePlayerManager players = new GamePlayerManager();
    private Instant startTime;
    private final List<Task> taskList = new ArrayList<>();
    private final VisibilityManager visibility = new VisibilityManager();
    private final GameWindowRegistry windowRegistry;
    private final Reference<World> world;

    public Game(ArcadePlugin plugin, int gameId, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.gameId = gameId;
        this.map = map;
        this.windowRegistry = new GameWindowRegistry(this);
        this.world = new WeakReference<>(world);
    }

    @Override
    public GamePlayer resolve(String username) {
        return this.players.resolve(username);
    }

    @Override
    public GamePlayer resolve(UUID uniqueId) {
        return this.players.resolve(uniqueId);
    }

    @Override
    public void send(String message) {
        this.plugin.getLogger().info("[Game] " + ChatColor.stripColor(message));
        for (GamePlayer player : this.players.getOnlinePlayers()) {
            player.send(message);
        }
    }

    @Override
    public void sendAction(String action) {
        this.plugin.getLogger().info("[Game/Action] " + ChatColor.stripColor(action));
        for (GamePlayer player : this.players.getOnlinePlayers()) {
            player.sendAction(action);
        }
    }

    @Override
    public void sendChat(String chat) {
        this.plugin.getLogger().info("[Game/Chat] " + ChatColor.stripColor(chat));
        for (GamePlayer player : this.players.getOnlinePlayers()) {
            player.sendChat(chat);
        }
    }

    public void sendGoalMessage(String message) {
        this.plugin.getLogger().info("[Game/Goal] " + ChatColor.stripColor(message));
        for (GamePlayer player : this.players.getOnlinePlayers()) {
            player.send(ChatColor.YELLOW + message);
            player.sendAction(ChatColor.YELLOW + message);
        }
    }

    public void enableModule(GameModule module) {
        if (module == null || module.getModule() == null || module.isEnabled()) {
            return;
        }

        this.getPlugin().getLogger().info("Enabling '" + module.getModule().getName() + "' module...");
        module.setEnabled(true);
        module.registerListeners();
        module.onEnable();
    }

    public GamePlayer findPlayer(String query) {
        ArcadePlayer player = this.plugin.findPlayer(query);
        return player != null ? player.getGamePlayer() : null;
    }

    public int getGameId() {
        return this.gameId;
    }

    public ArcadeMap getMap() {
        return this.map;
    }

    public GameModule getModule(Class<? extends Module<?>> module) {
        return this.getModules().getModule(module);
    }

    public GameModule getModuleById(String id) {
        return this.getModules().getModuleById(id);
    }

    public GameModuleContainer getModules() {
        return this.modules;
    }

    public GamePlayerManager getPlayers() {
        return this.players;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public VisibilityManager getVisibility() {
        return this.visibility;
    }

    public GameWindowRegistry getWindowRegistry() {
        return this.windowRegistry;
    }

    public World getWorld() {
        return this.world.get();
    }

    public void start() {
        this.startTime = Instant.now();

        for (GameModule module : this.getModules().getModules()) {
            this.enableModule(module);
        }

        this.plugin.getEventBus().publish(new GameStartEvent(this.plugin, this));

        this.plugin.getLogger().info("Game #" + this.getGameId() + " has started.");
    }

    public void stop() {
        this.plugin.getEventBus().publish(new GameStopEvent(this.plugin, this));

        for (GameModule module : this.getModules().getModules()) {
            module.setEnabled(false);
        }

        for (Task task : this.getTasks()) {
            task.cancelTask();
        }

        this.getWindowRegistry().removeAll();

        this.plugin.getLogger().info("Game #" + this.getGameId() + " has ended.");
    }

    //
    // Tasks
    //

    public int addAsyncTask(Task task) {
        int id = Task.DEFAULT_TASK_ID;
        if (!task.isTaskRunning() && this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleAsync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public int addSyncTask(Task task) {
        this.removeTask(task);

        int id = Task.DEFAULT_TASK_ID;
        if (!task.isTaskRunning() && this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleSync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public List<Countdown> getCountdowns() {
        List<Countdown> results = new ArrayList<>();
        for (Task task : this.getTasks()) {
            if (task instanceof Countdown) {
                results.add((Countdown) task);
            }
        }

        return results;
    }

    public List<Countdown> getRunningCountdowns() {
        List<Countdown> results = new ArrayList<>();
        for (Task task : this.getTasks()) {
            if (task.isTaskRunning() && task instanceof Countdown) {
                results.add((Countdown) task);
            }
        }

        return results;
    }

    public List<Task> getRunningTasks() {
        List<Task> results = new ArrayList<>();
        for (Task task : this.getTasks()) {
            if (task.isTaskRunning()) {
                results.add(task);
            }
        }

        return results;
    }

    public List<Task> getTasks() {
        for (Task task : new ArrayList<>(this.taskList)) {
            if (!task.isTaskRunning()) {
                this.removeTask(task);
            }
        }

        return this.taskList;
    }

    public boolean removeTask(Task task) {
        return this.taskList.remove(task);
    }
}
