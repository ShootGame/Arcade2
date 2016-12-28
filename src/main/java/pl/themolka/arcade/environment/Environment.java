package pl.themolka.arcade.environment;

import org.bukkit.event.Listener;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;

public class Environment extends SimpleEnvironmentListener implements Listener {
    public static final EnvironmentType DEFAULT_TYPE = EnvironmentType.DEVELOPMENT;

    private ArcadePlugin plugin;

    private boolean loaded = false;
    private final Element settings;
    private final EnvironmentType type;

    public Environment(Element settings, EnvironmentType type) {
        this.settings = settings;
        this.type = type;
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        plugin.getLogger().info("This server is starting in the " + type.prettyName() + " mode...");

        plugin.registerCommandObject(this);
        plugin.registerListenerObject(this);
    }

    public Element getSettings() {
        return this.settings;
    }

    public EnvironmentType getType() {
        return this.type;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
