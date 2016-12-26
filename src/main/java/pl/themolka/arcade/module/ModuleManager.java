package pl.themolka.arcade.module;

import pl.themolka.arcade.ArcadePlugin;

public class ModuleManager {
    private final ArcadePlugin plugin;

    private final ModuleContainer container = new ModuleContainer();

    public ModuleManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public ModuleContainer getContainer() {
        return this.container;
    }
}
