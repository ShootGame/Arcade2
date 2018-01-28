package pl.themolka.arcade.event;

import pl.themolka.arcade.ArcadePlugin;

public class PluginFreshEvent extends Event {
    public PluginFreshEvent(ArcadePlugin plugin) {
        super(plugin);
    }
}
