package pl.themolka.arcade.game;

import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameModule implements GameEvents, Listener {
    private ArcadePlugin plugin;

    private boolean loaded = false;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();

    public GameModule() {
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        List<Object> listeners = this.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                this.registerListenerObject(listener);
            }
        }

        this.registerListenerObject(this);
    }

    public final void destroy() {
        this.unregisterListenerObject(this);
        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return register;
    }

    public List<Object> getListenerObjects() {
        return this.listenerObjects;
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
}
