package pl.themolka.arcade;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.ArcadeCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.event.ArcadeEvents;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleContainer;
import pl.themolka.arcade.module.ModuleManager;
import pl.themolka.arcade.session.ArcadeSessions;
import pl.themolka.arcade.storages.ArcadeStorages;
import pl.themolka.arcade.xml.ModulesFile;
import pl.themolka.commons.Commons;
import pl.themolka.commons.command.Commands;
import pl.themolka.commons.event.Event;
import pl.themolka.commons.event.Events;
import pl.themolka.commons.generator.VoidGenerator;
import pl.themolka.commons.session.Sessions;
import pl.themolka.commons.storage.Storages;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Arcade main class
 */
public final class ArcadePlugin extends JavaPlugin {
    private Commons commons;
    private VoidGenerator generator;
    private ModuleManager modules;

    @Override
    public void onEnable() {
        this.commons = new ArcadeCommons(this);
        Event.setAutoEventPoster(this.getEvents());

        this.generator = new VoidGenerator();

        this.loadCommands();
        this.loadModules();
    }

    @Override
    public void onDisable() {
        // TODO body
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
        return this.generator;
    }

    public ArcadeCommands getCommands() {
        return (ArcadeCommands) this.getCommons().getCommands();
    }

    public ArcadeEvents getEvents() {
        return (ArcadeEvents) this.getCommons().getEvents();
    }

    public ModuleManager getModules() {
        return this.modules;
    }

    public void registerCommandObject(Object object) {
        this.getCommands().registerCommandObject(object);
    }

    public void registerListenerObject(Object object) {
        this.getEvents().register(object);

        if (object instanceof Listener) {
            this.getServer().getPluginManager().registerEvents((Listener) object, this);
        }
    }

    public void unregisterListenerObject(Object object) {
        this.getEvents().unregister(object);

        if (object instanceof Listener) {
            HandlerList.unregisterAll((Listener) object);
        }
    }

    private Commons getCommons() {
        return this.commons;
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

    private class ArcadeCommons implements Commons {
        private final Commands commands;
        private final Events events;
        private final Sessions<?> sessions;
        private final Storages storages;

        public ArcadeCommons(ArcadePlugin plugin) {
            this.commands = new ArcadeCommands(plugin);
            this.events = new ArcadeEvents();
            this.sessions = new ArcadeSessions(plugin);
            this.storages = new ArcadeStorages();
        }

        @Override
        public Commands getCommands() {
            return this.commands;
        }

        @Override
        public Events getEvents() {
            return this.events;
        }

        @Override
        public Sessions<?> getSessions() {
            return this.sessions;
        }

        @Override
        public Storages getStorages() {
            return this.storages;
        }
    }
}
