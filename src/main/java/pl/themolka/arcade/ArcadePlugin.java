package pl.themolka.arcade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
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
import pl.themolka.arcade.command.ArcadeCommands;
import pl.themolka.arcade.command.BukkitCommands;
import pl.themolka.arcade.command.ConsoleSender;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.InfoCommands;
import pl.themolka.arcade.command.MapCommands;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;
import pl.themolka.arcade.event.DeadListeners;
import pl.themolka.arcade.event.EventBus;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.PluginStartEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.game.SimpleGameManager;
import pl.themolka.arcade.generator.GeneratorType;
import pl.themolka.arcade.listener.BlockTransformListeners;
import pl.themolka.arcade.listener.GeneralListeners;
import pl.themolka.arcade.listener.ProtectionListeners;
import pl.themolka.arcade.map.Author;
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
import pl.themolka.arcade.module.ModulesLoadEvent;
import pl.themolka.arcade.permission.ClientPermissionStorage;
import pl.themolka.arcade.permission.Group;
import pl.themolka.arcade.permission.PermissionListeners;
import pl.themolka.arcade.permission.PermissionManager;
import pl.themolka.arcade.permission.PermissionsReloadEvent;
import pl.themolka.arcade.permission.PermissionsReloadedEvent;
import pl.themolka.arcade.permission.XMLPermissions;
import pl.themolka.arcade.scoreboard.ScoreboardListeners;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.Sessions;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.arcade.task.SimpleTaskListener;
import pl.themolka.arcade.task.SimpleTaskManager;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Tickable;
import pl.themolka.arcade.window.WindowListeners;
import pl.themolka.arcade.window.WindowRegistry;
import pl.themolka.arcade.xml.XMLLocation;

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
    public static final Author[] COPYRIGHTS = {
            new Author(UUID.fromString("2b5f34f6-fb05-4852-a86c-2e03bccbdf89"), "TheMolkaPL")
    };

    public static final String DEFAULT_SERVER_NAME = "The server";
    public static final String YAML_NOT_SUPPORTED = "YAML is not supported!";

    private BukkitCommands commands;
    private ConsoleSender console;
    private Environment environment;
    private EventBus eventBus;
    private GameManager games;
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final ManifestFile manifest = new ManifestFile();
    private MapManager maps;
    private final ModuleContainer modules = new ModuleContainer();
    private PermissionManager permissions;
    private final Map<UUID, ArcadePlayer> players = new HashMap<>();
    private final Map<String, Object> properties = new HashMap<>();
    private boolean running;
    private String serverName = DEFAULT_SERVER_NAME;
    private ServerSessionFile serverSession;
    private Settings settings;
    private Time startTime;
    private TaskManager tasks;
    private long tickId = 0L;
    private final List<Tickable> tickableList = new CopyOnWriteArrayList<>();
    private BukkitTask tickableTask;
    private WindowRegistry windowRegistry;

    //
    // Enable
    //

    @Override
    public void onEnable() {
        try {
            this.start();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not enable " + this.getDescription().getFullName() + ": " + th.getMessage(), th);
        }
    }

    public final void start() throws Throwable {
        if (this.isRunning()) {
            throw new IllegalStateException("Already running!");
        } else if (this.getStartTime() != null) {
            throw new IllegalStateException("Outdated class!");
        }

        this.running = true;
        this.startTime = Time.now();

        this.manifest.readManifestFile();
        this.eventBus = new EventBus(this);

        this.console = new ConsoleSender(this);

        this.commands = new BukkitCommands(this, this.getName());
        this.commands.setPrefix(BukkitCommands.BUKKIT_COMMAND_PREFIX);

        this.settings = new Settings(this);
        try {
            this.reloadConfig();
        } catch (RuntimeException ex) {
            ex.getCause().printStackTrace();
        }

        if (!this.getSettings().isEnabled()) {
            this.getLogger().log(Level.INFO, this.getName() + " isn't enabled in the settings file, skipped enabling...");
            return;
        }

        this.getEventBus().publish(new PluginStartEvent(this));
        this.loadServer();

        try {
            this.loadEnvironment();
            this.getEnvironment().onEnable();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not enable the environment.", th);
            th.printStackTrace();
            return;
        }

        this.loadPermissions();
        this.loadCommands();
        this.loadModules();
        this.loadMaps();
        this.loadTasks();
        this.loadWindows();
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

                if (ArcadePlugin.this.beginLogic()) {
                    getLogger().info(getName() + " is fresh and ready to use.");
                } else {
                    getLogger().severe("Could not start - see logs above. Shutting down the server...");
                    getServer().shutdown();
                }
            }
        }, 1L);
    }

    //
    // Disable
    //

    @Override
    public void onDisable() {
        if (!this.getSettings().isEnabled()) {
            return;
        }

        try {
            this.stop();
        } catch (Throwable th) {
            this.getLogger().log(Level.SEVERE, "Could not disable " + this.getDescription().getFullName() + ": " + th.getMessage(), th);
        }
    }

    public final void stop() throws Throwable {
        if (!this.isRunning()) {
            throw new IllegalStateException("Already not running!");
        }
        this.running = false;

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

            module.destroy();
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

    //
    // Inherited methods
    //

    @Override
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException(YAML_NOT_SUPPORTED);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
        return GeneratorType.getDefaultGenerator().getChunkGenerator();
    }

    @Override
    public void reloadConfig() {
        try {
            this.getSettings().setDocument(this.getSettings().readSettingsFile());
            this.getSettings().preprocess();

            // reload properties
            Element propertiesElement = this.getSettings().getData().getChild("properties");
            if (propertiesElement != null) {
                for (Element property : propertiesElement.getChildren()) {
                    this.setProperty(property.getName(), property.getTextNormalize());
                }
            }

            this.getEventBus().publish(new SettingsReloadEvent(this, this.getSettings()));
        } catch (IOException | JDOMException ex) {
            throw new RuntimeException(ex);
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
        throw new UnsupportedOperationException(YAML_NOT_SUPPORTED);
    }

    @Override
    public void saveDefaultConfig() {
        try {
            this.getSettings().copySettingsFile();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    //
    // Public methods
    //

    public void addPlayer(ArcadePlayer player) {
        this.players.put(player.getUuid(), player);
    }

    public void addTickable(Tickable tickable) {
        this.tickableList.add(tickable);
    }

    public final boolean beginLogic() {
        if (this.getGames().getQueue().hasNextMap()) {
            this.getGames().cycleNext();
            return true;
        } else {
            this.getLogger().severe("Could not cycle because the map queue is empty.");
            return false;
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

    public BukkitCommands getCommands() {
        return this.commands;
    }

    public ConsoleSender getConsole() {
        return this.console;
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

    public PermissionManager getPermissions() {
        return this.permissions;
    }

    public ArcadePlayer getPlayer(HumanEntity human) {
        if (human instanceof Player) {
            return this.getPlayer((Player) human);
        }

        return null;
    }

    public ArcadePlayer getPlayer(Player bukkit) {
        return this.getPlayer(bukkit.getUniqueId());
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
        return new ArrayList<>(this.players.values());
    }

    public Object getProperty(String key) {
        return this.getProperty(key, null);
    }

    public Object getProperty(String key, Object def) {
        return this.properties.getOrDefault(key, def);
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

    public Time getStartTime() {
        return this.startTime;
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

    public WindowRegistry getWindowRegistry() {
        return this.windowRegistry;
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public boolean isRunning() {
        return this.running;
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

    public void reloadPermissions() {
        PermissionManager permissions = this.getPermissions();

        try {
            permissions.setDocument(permissions.readPermissionsFile());
            this.getEventBus().publish(new PermissionsReloadEvent(this, permissions));

            permissions.clearGroups();
            permissions.setDefaultGroup(null);

            XMLPermissions xml = new XMLPermissions(permissions.getDocument());

            // groups
            for (Group group : xml.readGroups()) {
                permissions.addGroup(group);

                if (group.isDefault()) {
                    if (permissions.getDefaultGroup() != null) {
                        this.getLogger().log(Level.CONFIG, "Duplicate of default permission group found: " + group.getId());
                    } else {
                        permissions.setDefaultGroup(group);
                    }
                }
            }

            if (permissions.getDefaultGroup() == null) {
                this.getLogger().log(Level.CONFIG, "No default permission group found.");
                permissions.setDefaultGroup(new Group(Group.DEFAULT_FAMILY));
            }

            // players
            ClientPermissionStorage permissionStorage = xml.readPlayers(
                    new ArrayList<>(permissions.getGroups()), permissions.getDefaultGroup());
            permissions.setPermissionStorage(permissionStorage);

            this.getEventBus().publish(new PermissionsReloadedEvent(this, permissions));
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
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

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
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

    //
    // Private Loaders
    //

    private void loadCommands() {
        for (Object command : new Object[] {
                new ArcadeCommands(this),
                new GameCommands(this),
                new GeneralCommands(this),
                new InfoCommands(this),
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
        this.games = new SimpleGameManager(this);
        this.games.setGameId(this.getServerSession().getContent().getLastGameId());

        this.games.fillDefaultQueue();
    }

    private void loadMaps() {
        this.maps = new MapManager(this);
        this.maps.setParser(new XMLMapParser.XMLParserTechnology(this)); // map XML parser
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

                if (!ignoredModuleList.contains(infoAnnotation.id().toLowerCase())) {
                    moduleList.add(module);
                }
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }

        ModulesLoadEvent loadEvent = new ModulesLoadEvent(this, moduleList);
        this.getEventBus().publish(loadEvent);

        List<Module<?>> success = new ArrayList<>();
        for (Module<?> module : loadEvent.getModules()) {
            try {
                module.initialize(this);
                module.registerCommandObject(module);

                success.add(module);
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not load module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }

        this.getLogger().info("Successfully loaded " + success.size() + " of " + loadEvent.getModules().size() + " available modules.");
        this.getModules().register(success.toArray(new Module<?>[success.size()]));

        Element globalModules = this.getSettings().getData().getChild("modules");
        if (globalModules == null) {
            globalModules = new Element("modules");
        }

        for (Module<?> module : this.getModules().getModules()) {
            try {
                Element xml = globalModules.getChild(module.getId());
                module.setGlobal(xml != null);

                module.registerListeners();
                module.onEnable(xml);
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not enable module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }
    }

    private void loadPermissions() {
        this.permissions = new PermissionManager(this);
        this.reloadPermissions();
    }

    private void loadServer() {
        this.serverSession = new ServerSessionFile(this);
//        try {
//            this.serverSession.deserialize();
//        } catch (IOException io) {
//            this.getLogger().log(Level.SEVERE, "Could not load server-session file: " + io.getMessage(), io);
//        }

        Element serverElement = this.getSettings().getData().getChild("server");
        if (serverElement == null) {
            serverElement = new Element("server");
        }

        Attribute nameAttribute = serverElement.getAttribute("name");
        if (nameAttribute != null && this.serverName.equals(DEFAULT_SERVER_NAME)) {
            this.serverName = nameAttribute.getValue();
        }

        this.registerListenerObject(new BlockTransformListeners(this));
        this.registerListenerObject(new GeneralListeners(this));
        this.registerListenerObject(new ProtectionListeners(this));

        // dead events
        this.registerListenerObject(new DeadListeners(this));

        // permissions
        this.registerListenerObject(new PermissionListeners(this));

        // scoreboards
        this.registerListenerObject(new ScoreboardListeners(this));

        // sessions
        this.registerListenerObject(new Sessions(this));

        // windows
        this.registerCommandObject(new WindowListeners(this));
    }

    private void loadTasks() {
        this.tasks = new SimpleTaskManager(this);
        this.addTickable((Tickable) this.getTasks());

        this.getTasks().scheduleAsync(new SimpleTaskListener() {
            @Override
            public void onDay(long days) {
                getLogger().info("Server ran now for 24 hours. Will be restarting soon...");
                getGames().setNextRestart(true);
            }
        });
    }

    private void loadWindows() {
        this.windowRegistry = new WindowRegistry();
    }
}
