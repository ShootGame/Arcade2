package pl.themolka.arcade.game;

import org.bukkit.Server;
import org.bukkit.World;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
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
import java.util.logging.Level;

public class Game implements Metadata, Serializable {
    public static final String JSON_FILENAME = "game.json";

    private final transient ArcadePlugin plugin;

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
        if (modules != null) {
            this.readModules(modules);
        } else {
            this.plugin.getLogger().warning("No modules were found for '" + this.getMap().getMapInfo().getName() + "'.");
        }
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
        if (this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleAsync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public int addSyncTask(Task task) {
        int id = -1;
        if (this.taskList.add(task)) {
            id = this.plugin.getTasks().scheduleSync(task);
            task.setTaskId(id);
        }

        return id;
    }

    public void enableModule(GameModule module) {
        for (Class<? extends Module<?>> dependency : module.getModule().getDependency()) {
            ModuleInfo info = dependency.getAnnotation(ModuleInfo.class);
            if (info == null || !this.getModules().contains(dependency)) {
                break;
            } else if (!this.getModules().contains(dependency)) {
                try {
                    GameModule game = this.readModule(new Element(info.id()));
                    this.getModules().register(game);
                } catch (MapParserException ex) {
                    break;
                }
            }

            GameModule dependencyModule = this.getModules().getModule(dependency);
            this.enableModule(dependencyModule);
        }

        for (Class<? extends Module<?>> loadBefore : module.getModule().getLoadBefore()) {
            ModuleInfo info = loadBefore.getAnnotation(ModuleInfo.class);
            if (info == null || !this.getModules().contains(loadBefore)) {
                continue;
            }

            GameModule loadBeforeModule = this.getModules().getModule(loadBefore);
            this.enableModule(loadBeforeModule);
        }

        if (!this.hasDependencies(module)) {
            return;
        }

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

    public List<Countdown> gerRunningCountdowns() {
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

    public GameModule readModule(Element xml) throws MapParserException {
        ModuleContainer container = this.plugin.getModules();
        if (!container.contains(xml.getName())) {
            throw new MapParserException("module not found");
        }

        Module<?> module = this.plugin.getModules().getModuleById(xml.getName());
        try {
            Object object = module.buildGameModule(xml, this);
            if (object == null) {
                return null;
            }

            GameModule game = (GameModule) object;
            game.initialize(this.plugin, this, module);
            return game;
        } catch (ClassCastException cast) {
            throw new MapParserException("game module does not inherit the " + GameModule.class.getName());
        } catch (Throwable th) {
            throw new MapParserException("could not build game module", th);
        }
    }

    public void readModules(Element parent) {
        for (Element xml : parent.getChildren()) {
            try {
                GameModule module = this.readModule(xml);
                if (module != null) {
                    this.getModules().register(module);
                }
            } catch (MapParserException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not load module '" + xml.getName() + "': " + ex.getMessage(), ex);
            }
        }

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
                this.plugin.getLogger().log(Level.SEVERE, "Could not load module '" + global.getId() + "': " + ex.getMessage(), ex);
            }
        }
    }

    public boolean removeTask(Task task) {
        return this.taskList.remove(task);
    }

    public void start() {
        this.startTime = Instant.now();

        for (GameModule module : this.getModules().getModules()) {
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
}
