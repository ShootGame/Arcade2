package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.Countdown;
import pl.themolka.arcade.task.Task;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A game representation of the currently playing map.
 */
public class Game implements Messageable, PlayerVisibilityFilter {
    private final ArcadePlugin plugin;

    private final int gameId;
    private final ArcadeMap map;
    private final GameModuleContainer modules = new GameModuleContainer();
    private final GamePlayerManager players = new GamePlayerManager();
    private Instant startTime;
    private final List<Task> taskList = new ArrayList<>();
    private final List<PlayerVisibilityFilter> visibilityFilters = new ArrayList<>();
    private final GameWindowRegistry windowRegistry;
    private final Reference<World> world;

    /**
     * Create a new game and load modules from the configuration.
     * @param plugin The main class
     * @param gameId An unique identifier of this game in this server session.
     * @param map A map which this game will be playing on.
     * @param world A world which this game will be playing on.
     */
    public Game(ArcadePlugin plugin, int gameId, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.gameId = gameId;
        this.map = map;
        this.windowRegistry = new GameWindowRegistry(this);
        this.world = new WeakReference<>(world);

        this.readModules();
    }

    /**
     * Filters <code>target</code>s visibility by the <code>viewer</code> in this game.
     * @param viewer Viewer who can or cannot see the <code>target</code> player.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if <code>target</code> is visible, <code>false</code> otherwise.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        if (!viewer.isOnline() || !target.isOnline()) {
            return false;
        }

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

    public boolean addVisiblityFilter(PlayerVisibilityFilter filter) {
        return this.visibilityFilters.add(filter);
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
                    ex.printStackTrace();
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
        return player != null ? player.getGamePlayer() : null;
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

    public GamePlayer getPlayer(GamePlayerSnapshot snapshot) {
        return snapshot != null ? this.getPlayer(snapshot.getUuid()) : null;
    }

    public GamePlayer getPlayer(Player bukkit) {
        return bukkit != null ? this.getPlayer(bukkit.getUniqueId()) : null;
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        return uniqueId != null ? this.players.getPlayer(uniqueId) : null;
    }

    public GamePlayerManager getPlayers() {
        return this.players;
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

    public List<PlayerVisibilityFilter> getVisibilityFilters() {
        return new ArrayList<>(this.visibilityFilters);
    }

    public GameWindowRegistry getWindowRegistry() {
        return this.windowRegistry;
    }

    public World getWorld() {
        return this.world.get();
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
            throw new MapParserException(xml.getName() + ": module not found");
        }

        Module<?> module = this.plugin.getModules().getModuleById(xml.getName());
        try {
            Object object = module.buildGameModule(xml, this);
            if (object == null || !(object instanceof GameModule)) {
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

    public void readModules() {
        this.readMapModules();
        this.readGlobalModules();
    }

    public boolean removeTask(Task task) {
        return this.taskList.remove(task);
    }

    public boolean removeVisiblityFilter(PlayerVisibilityFilter filter) {
        return this.visibilityFilters.remove(filter);
    }

    public void sendGoalMessage(String message) {
        this.plugin.getLogger().info("[Goal] " + ChatColor.stripColor(message));
        for (GamePlayer player : this.players.getOnlinePlayers()) {
            player.send(ChatColor.YELLOW + message);
            player.sendAction(ChatColor.YELLOW + message);
        }
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

    private void readGlobalModules() {
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
                ex.printStackTrace();
            }
        }
    }

    private void readMapModules() {
        Node parent = Node.ofChildren("legacy", this.map.getManifest().getModules().getModules());

        if (parent == null || parent.isEmpty()) {
            this.plugin.getLogger().warning("No modules were found for '" + this.getMap().getMapInfo().getName() + "'.");
            return;
        }

        for (Node node : parent.children()) {
            try {
                GameModule module = this.readModule(toJdom(node));
                if (module != null) {
                    this.getModules().register(module);
                }
            } catch (MapParserException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Converting our DOM {@link Node}s into JDOM {@link Element}s.
     *
     * The Goal is to totally drop JDOM library from the source. We need to
     * convert our DOM objects to JDOM's to support legacy code. This class
     * should be used only for legacy purposes and ignored in new code.
     *
     * @deprecated Should be removed.
     */
    @Deprecated
    public static Element toJdom(Node node) {
        if (node == null) {
            return null;
        }

        Element element = new Element(node.getName());

        // attach properties
        for (Property property : node.properties()) {
            element.setAttribute(new Attribute(property.getName(), property.getValue()));
        }

        // attach body
        if (node.isPrimitive()) {
            element.setText(node.getValue());
        } else if (node.isTree()) {
            List<Element> children = new ArrayList<>();
            for (Node child : node.children()) {
                Element jdom = toJdom(child);

                if (jdom != null) {
                    children.add(jdom);
                }
            }

            element.addContent(children);
        }

        return element;
    }
}
