package pl.themolka.arcade.game;

import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModule implements GameEvents, Listener {
    private ArcadePlugin plugin;

    private Game game;
    private boolean loaded = false;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private Module<?> module;

    public GameModule() {
    }

    public final void initialize(ArcadePlugin plugin, Game game, Module<?> module) {
        if (this.isLoaded()) {
            return;
        }

        this.game = game;
        this.loaded = true;
        this.module = module;
        this.plugin = plugin;
    }

    public final void destroy() {
        this.unregisterListenerObject(this);
        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return register;
    }

    public Game getGame() {
        return this.game;
    }

    public List<Object> getListenerObjects() {
        return this.listenerObjects;
    }

    public Module<?> getModule() {
        return this.module;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void registerListenerObject(Object object) {
        if (this.listenerObjects.add(object)) {
            this.getPlugin().registerListenerObject(object);
        }
    }

    public void unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        this.listenerObjects.remove(object);
    }

    public void registerListeners() {
        if (!this.getListenerObjects().isEmpty()) {
            return;
        }

        List<Object> listeners = this.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                this.registerListenerObject(listener);
            }
        }

        this.registerListenerObject(this);
    }
}
