package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final ArcadePlugin plugin;

    private final ArcadeMap map;
    private final GameModuleContainer modules = new GameModuleContainer();

    public Game(ArcadePlugin plugin, ArcadeMap map) {
        this.plugin = plugin;

        this.map = map;
    }

    public ArcadeMap getMap() {
        return this.map;
    }

    public GameModuleContainer getModules() {
        return this.modules;
    }

    public void start() {
        for (GameModule module : this.getModules().getModules()) {
            this.registerListeners(module);
        }

        this.plugin.getEvents().post(new GameStartEvent(this.plugin, this));
    }

    public void stop() {
        this.plugin.getEvents().post(new GameStopEvent(this.plugin, this));

        for (GameModule module : this.getModules().getModules()) {
            this.unregisterListeners(module);
        }
    }

    private void registerListeners(GameModule module) {
        List<Object> listeners = module.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                module.registerListenerObject(listener);
            }
        }

        module.registerListenerObject(module);
    }

    private void unregisterListeners(GameModule module) {
        for (Object listener : module.getListenerObjects()) {
            module.unregisterListenerObject(listener);
        }
    }
}
