package pl.themolka.arcade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.ArcadeCommand;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.MapCommands;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;
import pl.themolka.arcade.event.EventBus;
import pl.themolka.arcade.event.Events;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.listener.GeneralListeners;
import pl.themolka.arcade.listener.ProtectionListeners;
import pl.themolka.arcade.map.MapContainerFillEvent;
import pl.themolka.arcade.map.MapContainerLoader;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.XMLMapParser;
import pl.themolka.arcade.metadata.ManifestFile;
import pl.themolka.arcade.metadata.ServerSessionFile;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModulesFile;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.Sessions;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.arcade.storage.Storages;
import pl.themolka.arcade.task.SimpleTaskListener;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.util.Tickable;
import pl.themolka.arcade.xml.XMLLocation;
import pl.themolka.commons.Commons;
import pl.themolka.commons.event.Event;
import pl.themolka.commons.generator.VoidGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
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

    public static final String DEFAULT_SERVER_NAME = "server";

    private ArcadeCommons commons;
    private Environment environment;
    private EventBus eventBus;
    private GameManager games;
    private final VoidGenerator generator = new VoidGenerator();
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final ManifestFile manifest = new ManifestFile();
    private MapManager maps;
    private final ModuleContainer modules = new ModuleContainer();
    private final Map<UUID, ArcadePlayer> players = new HashMap<>();
    private String serverName = DEFAULT_SERVER_NAME;
    private ServerSessionFile serverSession;
    private Settings settings;
    private TaskManager tasks;
    private long tickId = 0L;
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
        this.eventBus = new EventBus(this);

        this.settings = new Settings(this);
        this.reloadConfig();

        this.loadServer();

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
        this.loadTasks();
        this.loadGames();

        this.tickableTask = this.getServer().getScheduler().runTaskTimer(this, this, 1L, 1L);

        this.getEventBus().publish(new PluginReadyEvent(this));

        // begin the plugin logic
        this.getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Location spawn = XMLLocation.parse(ArcadePlugin.this.getSettings().getData().getChild("spawn"));

                    World defaultWorld = ArcadePlugin.this.getServer().getWorlds().get(0);
                    defaultWorld.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
                } catch (DataConversionException ignored) {
                }

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

        this.getEventBus().shutdown();

        try {
            this.getServerSession().serialize();
        } catch (IOException io) {
            this.getLogger().log(Level.SEVERE, "Could not save server-session file: " + io.getMessage(), io);
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
            this.getEventBus().publish(new SettingsReloadEvent(this, this.getSettings()));
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (Tickable tickable : this.getTickables()) {
            try {
                tickable.onTick(this.getTickId());
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not tick #" + this.getTickId() + " in " + tickable.getClass().getName(), th);
            }
        }

        this.tickId++;
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

    public <T> T deserializeJsonFile(File file, Class<T> object) throws IOException {
        try (Reader reader = new FileReader(file)) {
            return this.getGson().fromJson(reader, object);
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

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public GameManager getGames() {
        return this.games;
    }

    public VoidGenerator getGenerator() {
        return this.generator;
    }

    public Gson getGson() {
        return this.gson;
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

    public String getServerName() {
        return this.serverName;
    }

    public ServerSessionFile getServerSession() {
        return this.serverSession;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public TaskManager getTasks() {
        return this.tasks;
    }

    public long getTickId() {
        return this.tickId;
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
        this.getEventBus().subscribe(object);

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

    public void serializeJsonFile(File file, Serializable object) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            this.getGson().toJson(object, writer);
        }
    }

    public void setEnvironment(Environment environment) {
        if (this.getEnvironment() == null) {
            this.environment = environment;
        }
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void unregisterListenerObject(Object object) {
        this.getEventBus().unsubscribe(object);

        if (object instanceof Listener) {
            HandlerList.unregisterAll((Listener) object);
        }
    }

    private ArcadeCommons getCommons() {
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
        EnvironmentType type = EnvironmentType.forName(xml.getAttributeValue("type"));

        this.environment = type.buildEnvironment(xml);
        this.environment.initialize(this);
    }

    private void loadGames() {
        this.games = new GameManager(this);
        this.games.setGameId(this.getServerSession().getContent().getLastGameId());

        this.games.fillQueue();
    }

    private void loadMaps() {
        this.maps = new MapManager(this);
        this.maps.setParser(new XMLMapParser.XMLParserTechnology());
        this.maps.setWorldContainer(new File(this.getSettings().getData().getChild("world-container").getValue()));

        MapContainerFillEvent fillEvent = new MapContainerFillEvent(this);
        this.getEventBus().publish(fillEvent);

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
                module.registerCommandObject(module);

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

                module.setGlobal(true);
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not enable module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }
    }

    private void loadServer() {
        this.serverSession = new ServerSessionFile(this);
        try {
            this.serverSession.deserialize();
        } catch (IOException io) {
            this.getLogger().log(Level.SEVERE, "Could not load server-session file: " + io.getMessage(), io);
        }

        Element serverElement = this.getSettings().getData().getChild("server");
        if (serverElement == null) {
            serverElement = new Element("server");
        }

        Attribute nameAttribute = serverElement.getAttribute("name");
        if (nameAttribute != null) {
            this.serverName = nameAttribute.getValue();
        }

        this.registerListenerObject(new GeneralListeners(this));
        this.registerListenerObject(new ProtectionListeners(this));
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
        private final Sessions sessions;
        private final Storages storages;

        public ArcadeCommons(ArcadePlugin plugin) {
            this.commands = new Commands(plugin);
            this.events = new Events();
            this.sessions = new Sessions(plugin);
            this.storages = new Storages();

            this.commands.setSessions(this.sessions);
            Event.setAutoEventPoster(this.getEvents());
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
        public Sessions getSessions() {
            return this.sessions;
        }

        @Override
        public Storages getStorages() {
            return this.storages;
        }
    }
}
