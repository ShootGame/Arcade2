package pl.themolka.arcade.environment;

import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Node;

public class Environment extends SimpleEnvironmentListener implements Listener {
    public static final EnvironmentType DEFAULT_TYPE = EnvironmentType.DEVELOPMENT;

    private ArcadePlugin plugin;

    private boolean loaded = false;
    private final Node options;
    private final EnvironmentType type;

    public Environment(Node options, EnvironmentType type) {
        this.options = options;
        this.type = type;
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        plugin.getLogger().info("This server is starting in the " + this.type.toString() + " mode...");

        plugin.registerCommandObject(this);
        plugin.registerListenerObject(this);
    }

    public Node getOptions() {
        return this.options;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public EnvironmentType getType() {
        return this.type;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
