package pl.themolka.arcade;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import pl.themolka.arcade.bossbar.BossBarListeners;
import pl.themolka.arcade.command.ArcadeCommands;
import pl.themolka.arcade.command.BukkitCommands;
import pl.themolka.arcade.command.ConsoleSender;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.MapCommands;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.engine.EngineManager;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.event.DeadListeners;
import pl.themolka.arcade.event.EventBus;
import pl.themolka.arcade.event.PluginFreshEvent;
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
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModulesFile;
import pl.themolka.arcade.module.ModulesLoadEvent;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContainer;
import pl.themolka.arcade.parser.ParserManager;
import pl.themolka.arcade.parser.ParsersFile;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Silent;
import pl.themolka.arcade.scoreboard.ScoreboardListeners;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.Sessions;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.arcade.task.SimpleTaskListener;
import pl.themolka.arcade.task.SimpleTaskManager;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.ManifestFile;
import pl.themolka.arcade.util.Tickable;
import pl.themolka.arcade.window.WindowListeners;
import pl.themolka.arcade.window.WindowRegistry;

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
    public static final Author[] COPYRIGHTS = {
            Author.plain("2b5f34f6-fb05-4852-a86c-2e03bccbdf89", "TheMolkaPL")
    };

    public static final String DEFAULT_SERVER_NAME = "The server";
    public static final String YAML_NOT_SUPPORTED = "YAML is not supported!";

    private BukkitCommands commands;
    private ConsoleSender console;
    private EngineManager domEngines;
    private Environment environment;
    private EventBus eventBus;
    private GameManager games;
    private ManifestFile manifest;
    private MapManager maps;
    private final ModuleContainer modules = new ModuleContainer();
    private ParserManager parsers;
    private final Map<UUID, ArcadePlayer> players = new HashMap<>();
    private boolean running;
    private String serverName = DEFAULT_SERVER_NAME;
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

        this.loadParsers();

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(ManifestFile.DEFAULT_FILE)) {
            this.manifest = new ManifestFile(this.domEngines.forFile(ManifestFile.DEFAULT_FILE).read(input));
        }

        this.eventBus = new EventBus(this);

        this.console = new ConsoleSender(this);

        this.commands = new BukkitCommands(this, this.getName());
        this.commands.setPrefix(BukkitCommands.BUKKIT_COMMAND_PREFIX);

        this.settings = new Settings(this);
        this.reloadConfig();

        if (!this.getSettings().isEnabled()) {
            this.getLogger().log(Level.INFO, this.getName() + " isn't enabled in the settings file, skipped enabling...");
            return;
        }

        this.getEventBus().publish(new PluginStartEvent(this));
        this.loadServer();

        try {
            this.loadEnvironment();
            this.getEnvironment().onEnable();
        } catch (DOMException ex) {
            this.getLogger().log(Level.SEVERE, "Could not enable the environment: " + ex.toString());
            return;
        }

        this.loadCommands();
        this.loadModules();
        this.loadMaps();
        this.loadTasks();
        this.loadWindows();
        this.loadGames();

        this.tickableTask = this.getServer().getScheduler().runTaskTimer(this, this, 1L, 1L);

        this.getEventBus().publish(new PluginReadyEvent(this));

        // begin the plugin logic
        this.getServer().getScheduler().runTaskLater(this, () -> {
            World defaultWorld = this.getServer().getWorlds().get(0);
            Vector spawn = this.getSettings().getSpawn();
            defaultWorld.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

            if (this.beginLogic()) {
                this.getEventBus().publish(new PluginFreshEvent(this));
                this.getLogger().info(getName() + " is fresh and ready to use.");
            } else {
                this.getLogger().severe("Could not start - see logs above. Shutting down the server...");
                this.getServer().shutdown();
            }
        }, Time.ZERO.toTicks());
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
            this.getLogger().log(Level.SEVERE, "Could not disable environment " + this.getEnvironment().getType().toString(), th);
        }

        this.getEventBus().shutdown();
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
            this.settings.setDocument(this.settings.readSettingsFile());
            this.getEventBus().publish(new SettingsReloadEvent(this, this.settings));
        } catch (DOMException | IOException ex) {
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
            this.getLogger().severe("Could not cycle because map queue is empty.");
            return false;
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

    public EngineManager getDomEngines() {
        return this.domEngines;
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

    public ManifestFile getManifest() {
        return this.manifest;
    }

    public MapManager getMaps() {
        return this.maps;
    }

    public ModuleContainer getModules() {
        return this.modules;
    }

    public ParserManager getParsers() {
        return this.parsers;
    }

    public ArcadePlayer getPlayer(HumanEntity human) {
        if (human != null && human instanceof Player) {
            return this.getPlayer((Player) human);
        }

        return null;
    }

    public ArcadePlayer getPlayer(Player bukkit) {
        if (bukkit != null) {
            return this.getPlayer(bukkit.getUniqueId());
        }

        return null;
    }

    public ArcadePlayer getPlayer(String username) {
        if (username != null) {
            for (ArcadePlayer player : this.getPlayers()) {
                if (player.getUsername().equalsIgnoreCase(username)) {
                    return player;
                }
            }
        }

        return null;
    }

    public ArcadePlayer getPlayer(UUID uuid) {
        if (uuid != null) {
            return this.players.get(uuid);
        }

        return null;
    }

    public Collection<ArcadePlayer> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public String getServerName() {
        return this.serverName;
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
                new MapCommands(this)
        }) {
            this.registerCommandObject(command);
        }
    }

    private void loadEnvironment() throws DOMException {
        Node node = this.getSettings().getData().child("environment");

        Parser<Environment> parser = this.parsers.forType(Environment.class);
        if (parser == null) {
            throw new RuntimeException("No " + Environment.class.getSimpleName() + " parser installed");
        }

        this.environment = parser.parse(node).orFail();
        this.environment.initialize(this);
    }

    private void loadGames() {
        this.games = new SimpleGameManager(this);
        this.games.fillDefaultQueue();
    }

    private void loadMaps() {
        this.maps = new MapManager(this);
        this.maps.setWorldContainer(new File(this.getSettings().getData().child("world-container").getValue()));

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
        Node ignoredModules = this.getSettings().getData().child("ignored-modules");
        if (ignoredModules == null) {
            ignoredModules = Node.empty();
        }

        List<String> ignoredModuleList = new ArrayList<>();
        for (Node ignoredModule : ignoredModules.children("module")) {
            String id = ignoredModule.getValue();
            if (id != null && !id.isEmpty()) {
                ignoredModuleList.add(id);
            }
        }

        List<Module<?>> moduleList = new ArrayList<>();
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(ModulesFile.DEFAULT_FILENAME)) {
            ModulesFile file = new ModulesFile(this, input);

            for (Module<?> module : file.getModules()) {
                ModuleInfo infoAnnotation = module.getClass().getAnnotation(ModuleInfo.class);
                if (infoAnnotation == null) {
                    continue;
                }

                if (!ignoredModuleList.contains(infoAnnotation.id().toLowerCase())) {
                    moduleList.add(module);
                }
            }
        } catch (DOMException | IOException ex) {
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

        Node globalModules = this.getSettings().getData().child("modules");
        if (globalModules == null) {
            globalModules = Node.empty();
        }

        for (Module<?> module : this.getModules().getModules()) {
            try {
                Node moduleNode = globalModules.child(module.getId());
                module.setGlobal(moduleNode != null);

                module.registerListeners();
                module.onEnable(moduleNode);
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not enable module '" + module.getId() + "': " + th.getMessage(), th);
            }
        }
    }

    private void loadParsers() {
        this.domEngines = new EngineManager();
        this.domEngines.registerDefault();

        this.parsers = new ParserManager(this);
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(ParsersFile.DEFAULT_FILENAME)) {
            ParsersFile file = new ParsersFile(this, input);

            int done = 0;
            int doneSilent = 0;

            ParserContainer container = new ParserContainer();
            for (Class<? extends Parser<?>> parser : file.getParsers()) {
                try {
                    container.register(parser.newInstance());
                    done++;

                    Silent silent = parser.getAnnotation(Silent.class);
                    Produces produces = parser.getAnnotation(Produces.class);

                    if (produces == null) {
                        throw new ReflectiveOperationException(parser.getSimpleName() + " is not @Produces decorated!");
                    } else if (silent == null) {
                        Class<?> producesType = produces.value();
                        if (producesType != null) {
                            this.parsers.registerType(producesType, parser);
                        }
                    } else {
                        doneSilent++;
                    }
                } catch (ReflectiveOperationException ex) {
                    ex.printStackTrace();
                }
            }

            this.parsers.getContainer().register(container);

            String silentString = "";
            if (doneSilent > 0) {
                silentString = " (" + doneSilent + " of them " + (doneSilent == 1 ? "was" : "were") + " silent)";
            }

            this.getLogger().info("Registered " + done + " parsers" + silentString + ".");
        } catch (DOMException | IOException ex) {
            ex.printStackTrace();
        }

        this.parsers.install(); // Install parser dependencies
    }

    private void loadServer() {
        Node node = this.getSettings().getData().child("server");
        if (node == null) {
            node = Node.empty();
        }

        String name = node.propertyValue("name");
        if (name != null && this.serverName.equals(DEFAULT_SERVER_NAME)) {
            this.serverName = name;
        }

        this.registerListenerObject(new BlockTransformListeners(this));
        this.registerListenerObject(new GeneralListeners(this));
        this.registerListenerObject(new ProtectionListeners(this));

        // boss bars
        this.registerListenerObject(new BossBarListeners(this));

        // dead events
        this.registerListenerObject(new DeadListeners(this));

        // scoreboards
        this.registerListenerObject(new ScoreboardListeners(this));

        // sessions
        this.registerListenerObject(new Sessions(this));

        // windows
        this.registerListenerObject(new WindowListeners(this));
    }

    private void loadTasks() {
        this.tasks = new SimpleTaskManager(this);
        this.addTickable((Tickable) this.getTasks());

        this.getTasks().scheduleAsync(new SimpleTaskListener() {
            boolean done = false;

            @Override
            public void onDay(long days) {
                if (!this.done) {
                    getLogger().info("Server ran now for 24 hours. Will be restarting soon...");
                    getGames().setNextRestart(true);
                    this.done = true;
                }
            }
        });
    }

    private void loadWindows() {
        this.windowRegistry = new WindowRegistry();
    }
}
