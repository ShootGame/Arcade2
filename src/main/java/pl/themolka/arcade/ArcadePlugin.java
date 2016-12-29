package pl.themolka.arcade;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.ArcadeCommand;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.MapCommands;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.map.MapContainerFillEvent;
import pl.themolka.arcade.map.MapContainerLoader;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.MapQueue;
import pl.themolka.arcade.map.MapQueueFillEvent;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.map.XMLMapParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModulesFile;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.arcade.task.SimpleTaskListener;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.util.ManifestFile;
import pl.themolka.arcade.util.Tickable;
import pl.themolka.commons.Commons;
import pl.themolka.commons.command.Commands;
import pl.themolka.commons.event.Event;
import pl.themolka.commons.event.Events;
import pl.themolka.commons.generator.VoidGenerator;
import pl.themolka.commons.session.Sessions;
import pl.themolka.commons.storage.Storages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * The Arcade main class
 */
public final class ArcadePlugin extends JavaPlugin implements Runnable {
    public static final String[] COPYRIGHTS = {"TheMolkaPL"};

    private Commons commons;
    private Environment environment;
    private GameManager games;
    private final VoidGenerator generator = new VoidGenerator();
    private final ManifestFile manifest = new ManifestFile();
    private MapManager maps;
    private final ModuleContainer modules = new ModuleContainer();
    private final Map<UUID, ArcadePlayer> players = new HashMap<>();
    private Settings settings;
    private TaskManager tasks;
    private long tick = 0L;
    private final List<Tickable> tickableList = new CopyOnWriteArrayList<>();
    private BukkitTask tickableTask;

    @Override
    public void onEnable() {
        try {
            this.start();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not enable " + this.getDescription().getFullName() + ": " + th.getMessage(), th);
        }
    }

    public final void start() throws Throwable {
        this.manifest.readManifestFile();
        this.commons = new ArcadeCommons(this);
        Event.setAutoEventPoster(this.getEvents());

        this.settings = new Settings(this);
        this.reloadConfig();

        try {
            this.loadEnvironment();
            this.getEnvironment().onEnable();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not enable the environment.", th);
            th.printStackTrace();
            return;
        }

        this.loadCommands();
        this.loadModules();
        this.loadMaps();
        this.loadGames();
        this.loadTasks();

        this.tickableTask = this.getServer().getScheduler().runTaskTimer(this, this, 1L, 1L);

        this.getEvents().post(new PluginReadyEvent(this));

        // begin the plugin logic
        this.getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                ArcadePlugin.this.beginLogic();
            }
        }, 1L);
    }

    @Override
    public void onDisable() {
        try {
            this.stop();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not disable " + this.getDescription().getFullName() + ": " + th.getMessage(), th);
        }
    }

    public final void stop() throws Throwable {
        Game game = this.getGames().getCurrentGame();
        if (game != null) {
            this.getGames().destroyGame(game);
        }

        this.getTasks().cancelAll();
        this.getTickableTask().cancel();

        for (Module<?> module : this.getModules().getModules()) {
            try {
                module.onDisable();
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not disable module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }

        try {
            this.getEnvironment().onDisable();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not disable environment " + this.getEnvironment().getType().prettyName(), th);
        }
    }

    @Override
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException("YAML is not supported!");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
        return this.generator;
    }

    @Override
    public void reloadConfig() {
        try {
            this.getSettings().setDocument(this.getSettings().readSettingsFile());
            this.getEvents().post(new SettingsReloadEvent(this, this.getSettings()));
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (Tickable tickable : this.getTickables()) {
            try {
                tickable.onTick(this.getTick());
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not tick #" + this.getTick() + " in " + tickable.getClass().getName(), th);
            }
        }

        this.tick++;
    }

    @Override
    public void saveConfig() {
        throw new UnsupportedOperationException("YAML is not supported!");
    }

    @Override
    public void saveDefaultConfig() {
        try {
            this.getSettings().copySettingsFile();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void addPlayer(ArcadePlayer player) {
        this.players.put(player.getUuid(), player);
    }

    public void addTickable(Tickable tickable) {
        this.tickableList.add(tickable);
    }

    public final void beginLogic() {
        if (this.getGames().getQueue().hasNextMap()) {
            this.getGames().cycleNext();
        } else {
            this.getLogger().severe("Could not cycle because the map queue is empty.");
        }
    }

    public ArcadePlayer findPlayer(String query) {
        query = query.toLowerCase();
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getUsername().toLowerCase().contains(query)) {
                return player;
            }
        }

        return null;
    }

    public Commands getCommands() {
        return this.getCommons().getCommands();
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public Events getEvents() {
        return this.getCommons().getEvents();
    }

    public GameManager getGames() {
        return this.games;
    }

    public VoidGenerator getGenerator() {
        return this.generator;
    }

    public ManifestFile getManifest() {
        return this.manifest;
    }

    public MapManager getMaps() {
        return this.maps;
    }

    public ModuleContainer getModules() {
        return this.modules;
    }

    public ArcadePlayer getPlayer(Player bukkit) {
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getBukkit().equals(bukkit)) {
                return player;
            }
        }

        return null;
    }

    public ArcadePlayer getPlayer(String username) {
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }

        return null;
    }

    public ArcadePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    public Collection<ArcadePlayer> getPlayers() {
        return this.players.values();
    }

    public Settings getSettings() {
        return this.settings;
    }

    public TaskManager getTasks() {
        return this.tasks;
    }

    public long getTick() {
        return this.tick;
    }

    public List<Tickable> getTickables() {
        return this.tickableList;
    }

    public BukkitTask getTickableTask() {
        return this.tickableTask;
    }

    public void registerCommandObject(Object object) {
        this.getCommands().registerCommandObject(object);
    }

    public void registerListenerObject(Object object) {
        this.getEvents().register(object);

        if (object instanceof Listener) {
            this.getServer().getPluginManager().registerEvents((Listener) object, this);
        }
    }

    public void removePlayer(ArcadePlayer player) {
        this.removePlayer(player.getUuid());
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public boolean removeTickable(Tickable tickable) {
        return this.tickableList.remove(tickable);
    }

    public void setEnvironment(Environment environment) {
        if (this.getEnvironment() == null) {
            this.environment = environment;
        }
    }

    public void unregisterListenerObject(Object object) {
        this.getEvents().unregister(object);

        if (object instanceof Listener) {
            HandlerList.unregisterAll((Listener) object);
        }
    }

    private Commons getCommons() {
        return this.commons;
    }

    private void loadCommands() {
        for (Object command : new Object[] {
                new ArcadeCommand(this),
                new GameCommands(this),
                new GeneralCommands(this),
                new MapCommands(this)
        }) {
            this.registerCommandObject(command);
        }
    }

    private void loadEnvironment() throws JDOMException {
        Element xml = this.getSettings().getData().getChild("environment");
        EnvironmentType type = EnvironmentType.forName(xml.getTextNormalize());

        this.environment = type.buildEnvironment(xml);
        this.environment.initialize(this);
    }

    private void loadGames() {
        this.games = new GameManager(this);

        Element queueElement = this.getSettings().getData().getChild("queue");
        if (queueElement == null) {
            queueElement = new Element("queue");
        }

        MapQueue queue = this.getGames().getQueue();
        for (Element mapElement : queueElement.getChildren("map")) {
            String directory = mapElement.getAttributeValue("directory");
            String mapName = mapElement.getTextNormalize();

            OfflineMap map = null;
            if (directory != null) {
                map = this.getMaps().getContainer().getMapByDirectory(directory);
            } else if (mapName != null) {
                map = this.getMaps().getContainer().getMap(mapName);
            }

            if (map != null) {
                queue.addMap(map);
            }
        }

        this.getEvents().post(new MapQueueFillEvent(this, queue));
    }

    private void loadMaps() {
        this.maps = new MapManager(this);
        this.maps.setParser(new XMLMapParser.XMLParserTechnology());
        this.maps.setWorldContainer(new File(this.getSettings().getData().getChild("world-container").getValue()));

//        TODO fix this
//        try {
//            Location spawn = XMLLocation.parse(this.getSettings().getData().getChild("spawn"));
//
//            World defaultWorld = this.getServer().getWorlds().get(0);
//            defaultWorld.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
//        } catch (DataConversionException ignored) {
//        }

        MapContainerFillEvent fillEvent = new MapContainerFillEvent(this);
        this.getEvents().post(fillEvent);

        MapManager maps = this.getMaps();
        for (MapContainerLoader loader : fillEvent.getMapLoaderList()) {
            maps.addMapLoader(loader);
        }

        maps.getContainer().register(maps.getLoaderListContainer());
        this.getLogger().info("Loaded " + maps.getContainer().getMaps().size() + " map(s) into the plugin.");
    }

    private void loadModules() {
        Element ignoredModules = this.getSettings().getData().getChild("ignored-modules");
        if (ignoredModules == null) {
            ignoredModules = new Element("ignored-modules");
        }

        List<String> ignoredModuleList = new ArrayList<>();
        for (Element ignoredModule : ignoredModules.getChildren("module")) {
            String id = ignoredModule.getTextNormalize();
            if (id != null) {
                ignoredModuleList.add(id);
            }
        }

        List<Module<?>> moduleList = new ArrayList<>();
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(ModulesFile.DEFAULT_FILENAME)) {
            ModulesFile file = new ModulesFile(input);

            for (Module<?> module : file.getModules()) {
                ModuleInfo infoAnnotation = module.getClass().getAnnotation(ModuleInfo.class);
                if (infoAnnotation == null) {
                    continue;
                }

                if (!ignoredModuleList.contains(infoAnnotation.id())) {
                    moduleList.add(module);
                }
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }

        int success = 0;
        for (Module<?> module : moduleList) {
            try {
                module.initialize(this);
                success++;
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not load module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }

        this.getLogger().info("Successfully loaded " + success + " of " + moduleList.size() + " available modules.");
        this.getModules().register(moduleList.toArray(new Module<?>[moduleList.size()]));

        Element globalModules = this.getSettings().getData().getChild("modules");
        if (globalModules == null) {
            globalModules = new Element("modules");
        }

        for (Module<?> module : this.getModules().getModules()) {
            try {
                module.registerListeners();
                module.onEnable(globalModules.getChild(module.getId()));
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not enable module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }
    }

    private void loadTasks() {
        this.tasks = new TaskManager(this);
        this.addTickable(this.getTasks());

        this.getTasks().scheduleAsync(new SimpleTaskListener() {
            @Override
            public void onDay(long days) {
                ArcadePlugin plugin = ArcadePlugin.this;
                plugin.getLogger().info("Server ran now for 24 hours. Will be restarting soon...");
                plugin.getGames().setNextRestart(true);
            }
        });
    }

    private class ArcadeCommons implements Commons {
        private final Commands commands;
        private final Events events;
        private final Sessions<?> sessions;
        private final Storages storages;

        public ArcadeCommons(ArcadePlugin plugin) {
            this.commands = new pl.themolka.arcade.command.Commands(plugin);
            this.events = new pl.themolka.arcade.event.Events();
            this.sessions = new pl.themolka.arcade.session.Sessions(plugin);
            this.storages = new pl.themolka.arcade.storage.Storages();

            this.events.register(this.sessions);
            ArcadePlugin.this.getServer().getPluginManager()
                    .registerEvents((pl.themolka.arcade.session.Sessions) this.sessions, ArcadePlugin.this);
        }

        @Override
        public Commands getCommands() {
            return this.commands;
        }

        @Override
        public Events getEvents() {
            return this.events;
        }

        @Override
        public Sessions<?> getSessions() {
            return this.sessions;
        }

        @Override
        public Storages getStorages() {
            return this.storages;
        }
    }
}
