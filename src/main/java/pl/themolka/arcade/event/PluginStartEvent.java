package pl.themolka.arcade.event;

import pl.themolka.arcade.ArcadePlugin;

public class PluginStartEvent extends Event {
    public PluginStartEvent(ArcadePlugin plugin) {
        super(plugin);
    }
}
