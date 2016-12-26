package pl.themolka.arcade.event;

import pl.themolka.arcade.ArcadePlugin;

public class PluginReadyEvent extends Event {
    public PluginReadyEvent(ArcadePlugin plugin) {
        super(plugin);
    }
}
