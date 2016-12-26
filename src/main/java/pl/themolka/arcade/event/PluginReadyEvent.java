package pl.themolka.arcade.event;

import pl.themolka.arcade.ArcadePlugin;

public class PluginReadyEvent extends ArcadeEvent {
    public PluginReadyEvent(ArcadePlugin plugin) {
        super(plugin);
    }
}
