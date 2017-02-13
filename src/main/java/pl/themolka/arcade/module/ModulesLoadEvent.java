package pl.themolka.arcade.module;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

import java.util.List;

/**
 * Called when modules are being to load.
 */
public class ModulesLoadEvent extends Event {
    private final List<Module<?>> modules;

    public ModulesLoadEvent(ArcadePlugin plugin, List<Module<?>> modules) {
        super(plugin);

        this.modules = modules;
    }

    public List<Module<?>> getModules() {
        return this.modules;
    }
}
