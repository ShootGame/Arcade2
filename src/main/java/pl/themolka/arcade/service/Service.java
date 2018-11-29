package pl.themolka.arcade.service;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;

public abstract class Service implements ServerListener, Listener {
    private ArcadePlugin plugin;

    public Service() {
    }

    public final void initalize(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.plugin.getServer();
    }
}
