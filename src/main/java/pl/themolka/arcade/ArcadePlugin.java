package pl.themolka.arcade;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleManager;
import pl.themolka.arcade.xml.ModulesFile;
import pl.themolka.commons.BukkitCommonsFactory;
import pl.themolka.commons.Commons;
import pl.themolka.commons.generator.VoidGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Arcade main class
 */
public final class ArcadePlugin extends JavaPlugin {
    private Commons commons;

    private ModuleManager modules;

    @Override
    public void onEnable() {
        this.commons = BukkitCommonsFactory.bukkitFactory(this).build(); // TODO custom

        this.loadCommands();
        this.loadModules();
    }

    @Override
    public void onDisable() {
        // TODO body
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
        return new VoidGenerator();
    }

    public Commons getCommons() {
        return this.commons;
    }

    public ModuleManager getModules() {
        return this.modules;
    }

    public void registerCommandObject(Object object) {
        this.getCommons().getCommands().registerCommandObject(object);
    }

    public void registerListenerObject(Object object) {
        this.getCommons().getEvents().register(object);

        if (object instanceof Listener) {
            this.getServer().getPluginManager().registerEvents((Listener) object, this);
        }
    }

    public void unregisterListenerObject(Object object) {
        this.getCommons().getEvents().unregister(object);

        if (object instanceof Listener) {
            HandlerList.unregisterAll((Listener) object);
        }
    }

    private void loadCommands() {
        for (Object command : new Object[] {
                new GeneralCommands(this)
        }) {
            this.registerCommandObject(command);
        }
    }

    private void loadModules() {
        this.modules = new ModuleManager(this);

        List<Module<?>> moduleList = new ArrayList<>();
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(ModulesFile.DEFAULT_FILENAME)) {
            ModulesFile file = new ModulesFile(input);
            moduleList.addAll(file.getModules(file.getRoot()));
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }

        for (Module<?> module : moduleList) {
            module.initialize(this);
        }

        ModuleContainer container = new ModuleContainer();
        container.register(moduleList.toArray(new Module<?>[moduleList.size()]));

        this.getModules().setContainer(container);
    }
}
