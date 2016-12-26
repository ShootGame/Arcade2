package pl.themolka.arcade.module;

import pl.themolka.arcade.ArcadePlugin;

public class ModuleManager {
    private final ArcadePlugin plugin;

    private ModuleContainer container;

    public ModuleManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public ModuleContainer getContainer() {
        return this.container;
    }

    public void setContainer(ModuleContainer container) {
        this.container = container;
    }
}
