package pl.themolka.arcade.game;

import org.bukkit.World;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

public class Game {
    private final ArcadePlugin plugin;

    private final ArcadeMap map;
    private final GameModuleContainer modules = new GameModuleContainer();
    private final World world;

    public Game(ArcadePlugin plugin, ArcadeMap map, World world) {
        this.plugin = plugin;

        this.map = map;
        this.world = world;
    }

    public ArcadeMap getMap() {
        return this.map;
    }

    public GameModuleContainer getModules() {
        return this.modules;
    }

    public World getWorld() {
        return this.world;
    }

    public void start() {
        for (GameModule module : this.getModules().getModules()) {
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
}
