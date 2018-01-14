package pl.themolka.arcade.game;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapError;
import pl.themolka.arcade.map.MapParserError;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.scoreboard.ScoreboardContext;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.Countdown;
import pl.themolka.arcade.task.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A game representation of the currently playing map.
 */
public class Game implements Metadata, PlayerVisibilityFilter {
    private final ArcadePlugin plugin;

    private final List<MapError> errors = new ArrayList<>();
    private final int gameId;
    private final ArcadeMap map;
    private final MetadataContainer metadata = new MetadataContainer();
    private final GameModuleContainer modules = new GameModuleContainer();
    private final List<GamePlayerListener> playerListeners = new ArrayList<>();
    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final ScoreboardContext scoreboard;
    private Instant startTime;
    private final List<Task> taskList = new ArrayList<>();
    private final List<PlayerVisibilityFilter> visibilityFilters = new ArrayList<>();
    private final GameWindowRegistry windowRegistry;
    private final World world;

    /**
     * Create a new game and load modules from the configuration.
     * @param plugin The main class
     * @param gameId An unique identifier of this game in this server session.
     * @param map A map which this game will be playing on.
     * @param world A world which this game will be playing on.
     *
     * TODO: Add multi-world support in the future.
     * Really - we should add an ability to play the same game on multiple maps...
     * To do this - we should add a {@link Map} containg a {@link World} as a key
     *              and {@link Game} as a value. All events should be handled per
     *              playing game.
     */
    public Game(ArcadePlugin plugin, int gameId, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.gameId = gameId;
        this.map = map;
        this.scoreboard = new ScoreboardContext(plugin, this);
        this.windowRegistry = new GameWindowRegistry(this);
        this.world = world;

        Element modules = map.getConfiguration().getRoot().getChild("modules");
        if (modules == null) {
            this.plugin.getLogger().warning("No modules were found for '" + this.getMap().getMapInfo().getName() + "'.");
            modules = new Element("modules");
        }

        this.readModules(modules);
    }

    /**
     * Filters <code>target</code>s visibility by the <code>viewer</code> in this game.
     * @param viewer Viewer who can or cannot see the <code>target</code> player.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if <code>target</code> is visible, <code>false</code> otherwise.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        boolean def = PlayerVisibilityFilter.DEFAULT.canSee(viewer, target);
        for (PlayerVisibilityFilter filter : this.visibilityFilters) {
            boolean value = filter.canSee(viewer, target);
            if (value != def) {
                return value;
            }
        }

        return def;
    }

    @Override
    public Object getMetadata(Class<? extends Module<?>> owner, String key, Object def) {
        return this.metadata.getMetadata(owner, key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.metadata.getMetadataKeys();
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
    }

    public int addAsyncTask(Task task) {
        int id = Task.DEFAULT_TASK_ID;
        if (!task.isTaskRunning() && this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleAsync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public boolean addError(MapError error) {
        return this.errors.add(error);
    }

    public boolean addError(MapParserException exception) {
        return this.addError(new MapParserError(this.getMap(), exception));
    }

    public void addPlayer(GamePlayer player) {
        for (GamePlayerListener notify : this.playerListeners) {
            notify.notifyRegister();
        }

        this.players.put(player.getUuid(), player);

        for (GamePlayerListener notify : this.playerListeners) {
            notify.notifyRegistered();
        }
    }

    public boolean addPlayerListener(GamePlayerListener listener) {
        return this.playerListeners.add(listener);
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

    public boolean addVisiblityFilter(PlayerVisibilityFilter filter) {
        return this.visibilityFilters.add(filter);
    }

    public boolean clearErrors() {
        if (this.hasErrors()) {
            this.errors.clear();
            return true;
        }

        return false;
    }

    public void enableModule(GameModule module) {
        if (module == null || module.getModule() == null || module.isEnabled()) {
            return;
        }

        for (Class<? extends Module<?>> dependency : module.getModule().getDependency()) {
            ModuleInfo info = dependency.getAnnotation(ModuleInfo.class);
            if (info == null) {
                break;
            } else if (!this.getModules().contains(dependency)) {
                try {
                    GameModule game = this.readModule(new Element(info.id().toLowerCase()));
                    if (game != null) {
                        this.getModules().register(game);
                    }
                } catch (MapParserException ex) {
                    this.addError(ex);
                }
            }

            GameModule dependencyModule = this.getModules().getModule(dependency);
            this.enableModule(dependencyModule);
        }

        for (Class<? extends Module<?>> loadBefore : module.getModule().getLoadBefore()) {
            ModuleInfo info = loadBefore.getAnnotation(ModuleInfo.class);
            if (info == null) {
                continue;
            }

            GameModule loadBeforeModule = this.getModules().getModule(loadBefore);
            this.enableModule(loadBeforeModule);
        }

        if (!this.hasDependencies(module)) {
            return;
        }

        this.getPlugin().getLogger().info("Enabling '" + module.getModule().getName() + "' module...");
        module.registerListeners();
        module.onEnable();
        module.setEnabled(true);
    }

    public GamePlayer findPlayer(String query) {
        ArcadePlayer player = this.plugin.findPlayer(query);
        if (player != null) {
            return player.getGamePlayer();
        }

        return null;
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

    public List<MapError> getErrors() {
        return this.errors;
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

    public GamePlayer getPlayer(ArcadePlayer player) {
        if (player != null) {
            return this.getPlayer(player.getUuid());
        }

        return null;
    }

    public GamePlayer getPlayer(GamePlayerSnapshot snapshot) {
        if (snapshot != null) {
            return this.getPlayer(snapshot.getUuid());
        }

        return null;
    }

    public GamePlayer getPlayer(Player bukkit) {
        if (bukkit != null) {
            return this.getPlayer(bukkit.getUniqueId());
        }

        return null;
    }

    public GamePlayer getPlayer(String username) {
        ArcadePlayer player = this.plugin.getPlayer(username);
        if (player != null) {
            return player.getGamePlayer();
        }

        return null;
    }

    public GamePlayer getPlayer(UUID uuid) {
        if (uuid != null) {
            return this.players.get(uuid);
        }

        return null;
    }

    /**
     * Returns a {@link Collection} of players <bold>ever</bold> played in this game.
     * @deprecated Use {@link ArcadePlugin#getPlayers()} for online players instead.
     */
    @Deprecated
    public Collection<GamePlayer> getPlayers() {
        return this.players.values();
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
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

    public ScoreboardContext getScoreboard() {
        return this.scoreboard;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public List<Task> getTasks() {
        for (Task task : new ArrayList<>(this.taskList)) {
            if (!task.isTaskRunning()) {
                this.removeTask(task);
            }
        }

        return this.taskList;
    }

    public List<PlayerVisibilityFilter> getVisibilityFilters() {
        return new ArrayList<>(this.visibilityFilters);
    }

    public GameWindowRegistry getWindowRegistry() {
        return this.windowRegistry;
    }

    public World getWorld() {
        return this.world;
    }

    public boolean hasDependencies(GameModule module) {
        for (Class<? extends Module<?>> dependency : module.getModule().getDependency()) {
            if (!this.getModules().contains(dependency)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public GameModule readModule(Element xml) throws MapParserException {
        ModuleContainer container = this.plugin.getModules();
        if (!container.contains(xml.getName())) {
            throw new MapParserException(xml.getName() + ": module not found");
        }

        Module<?> module = this.plugin.getModules().getModuleById(xml.getName());
        try {
            Object object = module.buildGameModule(xml, this);
            if (object == null) {
                return null;
            }

            GameModule game = (GameModule) object;
            game.initialize(this.plugin, this, module, xml);
            return game;
        } catch (ClassCastException cast) {
            throw new MapParserException(module, "game module does not inherit the " + GameModule.class.getName());
        } catch (Throwable th) {
            throw new MapParserException(module, "could not build game module", th);
        }
    }

    public void readModules(Element parent) {
        this.readMapModule(parent);
        this.readGlobalModule();
    }

    public void removePlayer(GamePlayer player) {
        this.removePlayer(player.getUuid());
    }

    public void removePlayer(UUID uuid) {
        if (this.players.containsKey(uuid)) {
            for (GamePlayerListener notify : this.playerListeners) {
                notify.notifyUnregister();
            }

            this.players.remove(uuid);

            for (GamePlayerListener notify : this.playerListeners) {
                notify.notifyUnregistered();
            }
        }
    }

    public boolean removePlayerListener(GamePlayerListener listener) {
        return this.playerListeners.remove(listener);
    }

    public boolean removeTask(Task task) {
        return this.taskList.remove(task);
    }

    public boolean removeVisiblityFilter(PlayerVisibilityFilter filter) {
        return this.visibilityFilters.remove(filter);
    }

    public void start() {
        this.startTime = Instant.now();

        for (GameModule module : new ArrayList<>(this.getModules().getModules())) {
            this.enableModule(module);
        }

        this.plugin.getEventBus().publish(new GameStartEvent(this.plugin, this));

        this.plugin.getLogger().info("Game #" + (this.getGameId() + 1) + " has started.");
    }

    public void stop() {
        this.plugin.getEventBus().publish(new GameStopEvent(this.plugin, this));

        for (GameModule module : this.getModules().getModules()) {
            module.setEnabled(false);
            module.onDisable();
            module.destroy();
        }

        for (Task task : this.getTasks()) {
            task.cancelTask();
        }

        this.getWindowRegistry().removeAll();

        this.plugin.getLogger().info("Game #" + (this.getGameId() + 1) + " has ended.");
    }

    private void readGlobalModule() {
        for (Module<?> global : this.plugin.getModules().getModules()) {
            if (!global.isGlobal() || this.getModules().contains(global.getId())) {
                continue;
            }

            try {
                GameModule module = this.readModule(new Element(global.getId()));
                if (module != null) {
                    this.getModules().register(module);
                }
            } catch (MapParserException ex) {
                this.addError(ex);
            }
        }
    }

    private void readMapModule(Element parent) {
        for (Element xml : parent.getChildren()) {
            try {
                GameModule module = this.readModule(xml);
                if (module != null) {
                    this.getModules().register(module);
                }
            } catch (MapParserException ex) {
                this.addError(ex);
                ex.printStackTrace();
            }
        }
    }
}
