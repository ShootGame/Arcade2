package pl.themolka.arcade.game;

import org.bukkit.World;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Game {
    private final ArcadePlugin plugin;

    private final ArcadeMap map;
    private final GameModuleContainer modules = new GameModuleContainer();
    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final World world;

    public Game(ArcadePlugin plugin, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.map = map;
        this.world = world;

        this.readModules(map.getConfiguration().getChild("modules"));
    }

    public void addPlayer(GamePlayer player) {
        this.players.put(player.getUuid(), player);
    }

    public ArcadeMap getMap() {
        return this.map;
    }

    public GameModuleContainer getModules() {
        return this.modules;
    }

    public GamePlayer getPlayer(String username) {
        for (GamePlayer player : this.getPlayers()) {
            if (player.getUsername().equals(username)) {
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

    public World getWorld() {
        return this.world;
    }

    public void start() {
        for (GameModule module : this.getModules().getModules()) {
            for (Class<? extends Module<?>> dependency : module.getModule().getDependency()) {
                if (!this.getModules().contains(dependency)) {
                    return;
                }
            }

            module.registerListeners();
            module.onEnable();
        }

        this.plugin.getEvents().post(new GameStartEvent(this.plugin, this));
    }

    public void stop() {
        this.plugin.getEvents().post(new GameStopEvent(this.plugin, this));

        for (GameModule module : this.getModules().getModules()) {
            module.onDisable();
            module.destroy();
        }
    }

    private GameModule readModule(Element xml) throws MapParserException {
        ModuleContainer container = this.plugin.getModules().getContainer();
        if (container.contains(xml.getName())) {
            throw new MapParserException("module not found");
        }

        Module<?> module = this.plugin.getModules().getContainer().getModuleById(xml.getName());
        try {
            Object object = module.buildGameModule(xml);
            if (object == null) {
                return null;
            }

            if (!object.getClass().isAssignableFrom(GameModule.class)) {
                throw new MapParserException("game module must extend " + GameModule.class.getName());
            }

            GameModule game = (GameModule) object;
            game.initialize(this.plugin, this, module);
            return game;
        } catch (Throwable th) {
            throw new MapParserException("could not build game module", th);
        }
    }

    private void readModules(Element parent) {
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
    }
}
