package pl.themolka.arcade.game;

import org.bukkit.Server;
import org.bukkit.World;
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
import pl.themolka.arcade.task.Countdown;
import pl.themolka.arcade.task.Task;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Game implements Metadata, Serializable {
    public static final String JSON_FILENAME = "game.json";

    private final transient ArcadePlugin plugin;

    private final List<MapError> errors = new ArrayList<>();
    private final ArcadeMap map;
    private final transient MetadataContainer metadata = new MetadataContainer();
    private final transient GameModuleContainer modules = new GameModuleContainer();
    private final transient Map<UUID, GamePlayer> players = new HashMap<>();
    private Instant startTime;
    private final transient List<Task> taskList = new ArrayList<>();
    private final transient World world;

    public Game(ArcadePlugin plugin, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.map = map;
        this.world = world;

        Element modules = map.getConfiguration().getRoot().getChild("modules");
        if (modules == null) {
            this.plugin.getLogger().warning("No modules were found for '" + this.getMap().getMapInfo().getName() + "'.");
            modules = new Element("modules");
        }

        this.readModules(modules);
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

    public void addPlayer(GamePlayer player) {
        this.players.put(player.getUuid(), player);
    }

    public int addAsyncTask(Task task) {
        int id = -1;
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

    public int addSyncTask(Task task) {
        this.removeTask(task);

        int id = -1;
        if (!task.isTaskRunning() && this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleSync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public boolean clearErrors() {
        if (this.hasErrors()) {
            this.errors.clear();
            return true;
        }

        return false;
    }

    public void enableModule(GameModule module) {
        if (module == null || module.getModule() == null) {
            return;
        }

        for (Class<? extends Module<?>> dependency : module.getModule().getDependency()) {
            ModuleInfo info = dependency.getAnnotation(ModuleInfo.class);
            if (info == null || this.getModules().contains(dependency)) {
                break;
            } else if (!this.getModules().contains(dependency)) {
                try {
                    GameModule game = this.readModule(new Element(info.id()));
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
            if (info == null || this.getModules().contains(loadBefore)) {
                continue;
            }

            GameModule loadBeforeModule = this.getModules().getModule(loadBefore);
            this.enableModule(loadBeforeModule);
        }

        if (!this.hasDependencies(module)) {
            return;
        }

        this.getPlugin().getLogger().info("Enabling '" + module.getModule().getId() + "' module...");
        module.registerListeners();
        module.onEnable();
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

    public GamePlayer getPlayer(String username) {
        for (GamePlayer player : this.getPlayers()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }

        return null;
    }

    public GamePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

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
        this.players.remove(uuid);
    }

    public boolean removeTask(Task task) {
        return this.taskList.remove(task);
    }

    public void start() {
        this.startTime = Instant.now();

        for (GameModule module : new ArrayList<>(this.getModules().getModules())) {
            this.enableModule(module);
        }

        this.plugin.getEventBus().publish(new GameStartEvent(this.plugin, this));
    }

    public void stop() {
        this.plugin.getEventBus().publish(new GameStopEvent(this.plugin, this));

        for (GameModule module : this.getModules().getModules()) {
            module.onDisable();
            module.destroy();
        }

        for (Task task : this.getTasks()) {
            task.cancelTask();
        }
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
            }
        }
    }
}
