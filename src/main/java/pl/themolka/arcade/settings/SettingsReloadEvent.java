package pl.themolka.arcade.settings;

import pl.themolka.arcade.ArcadePlugin;

public class SettingsReloadEvent extends SettingsEvent {
    public SettingsReloadEvent(ArcadePlugin plugin, Settings settings) {
        super(plugin, settings);
    }
}
