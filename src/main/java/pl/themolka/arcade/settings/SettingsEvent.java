package pl.themolka.arcade.settings;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class SettingsEvent extends Event {
    private final Settings settings;

    public SettingsEvent(ArcadePlugin plugin, Settings settings) {
        super(plugin);

        this.settings = settings;
    }

    public Settings getSettings() {
        return this.settings;
    }
}
