package pl.themolka.arcade.service;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;

import java.util.Objects;

public abstract class Service implements ServerListener, Listener {
    private ArcadePlugin plugin;

    private String id;
    private State state;

    enum State {
        NEW, INITIALIZED, LOADED, UNLOADED
    }

    public Service() {
        this.state = State.NEW;
    }

    public final void initalize(String id, ArcadePlugin plugin) {
        if (!this.state.equals(State.NEW)) {
            throw new IllegalStateException();
        }

        this.id = id;
        this.plugin = plugin;

        this.state = State.INITIALIZED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public final void load() {
        if (!this.state.equals(State.INITIALIZED)) {
            throw new IllegalStateException();
        }

        this.loadService();
        this.plugin.registerListenerObject(this);

        this.state = State.LOADED;
    }

    @Override
    public final void unload() {
        if (!this.state.equals(State.LOADED)) {
            throw new IllegalStateException();
        }

        this.plugin.unregisterListenerObject(this);
        this.unloadService();

        this.state = State.UNLOADED;
    }

    protected void loadService() {
    }

    protected void unloadService() {
    }

    public String getId() {
        return this.id;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.plugin.getServer();
    }

    public boolean isLoaded() {
        return this.state.equals(State.LOADED);
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
