package pl.themolka.arcade;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.ArcadeCommand;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.MapCommands;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.XMLMapParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleManager;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.xml.ManifestFile;
import pl.themolka.arcade.xml.ModulesFile;
import pl.themolka.arcade.xml.SettingsFile;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Arcade main class
 */
public final class ArcadePlugin extends JavaPlugin {
    public static final String[] COPYRIGHTS = {"TheMolkaPL"};

    private Commons commons;
    private GameManager games;
    private VoidGenerator generator;
    private ManifestFile manifest;
    private MapManager maps;
    private ModuleManager modules;
    private final Map<UUID, ArcadePlayer> players = new HashMap<>();
    private SettingsFile settings;

    @Override
    public void onEnable() {
        this.manifest = new ManifestFile();
        this.manifest.readManifestFile();

        this.commons = new ArcadeCommons(this);
        Event.setAutoEventPoster(this.getEvents());

        this.generator = new VoidGenerator();

        this.settings = new SettingsFile(this);
        this.reloadConfig();

        this.loadCommands();
        this.loadGames();
        this.loadMaps();
        this.loadModules();

        this.getEvents().post(new PluginReadyEvent(this));
    }

    @Override
    public void onDisable() {
        // TODO body
    }

    @Override
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException("YAML is not supported!");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
        return this.generator;
    }

    @Override
    public void reloadConfig() {
        try {
            this.getSettings().readSettingsFile();
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveConfig() {
        throw new UnsupportedOperationException("YAML is not supported!");
    }

    @Override
    public void saveDefaultConfig() {
        try {
            this.getSettings().copySettingsFile();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public ArcadePlayer findPlayer(String query) {
        query = query.toLowerCase();
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getUsername().toLowerCase().contains(query)) {
                return player;
            }
        }

        return null;
    }

    public Commands getCommands() {
        return (Commands) this.getCommons().getCommands();
    }

    public Events getEvents() {
        return (Events) this.getCommons().getEvents();
    }

    public GameManager getGames() {
        return this.games;
    }

    public VoidGenerator getGenerator() {
        return this.generator;
    }

    public ManifestFile getManifest() {
        return this.manifest;
    }

    public MapManager getMaps() {
        return this.maps;
    }

    public ModuleManager getModules() {
        return this.modules;
    }

    public ArcadePlayer getPlayer(Player bukkit) {
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getBukkit().equals(bukkit)) {
                return player;
            }
        }

        return null;
    }

    public ArcadePlayer getPlayer(String username) {
        for (ArcadePlayer player : this.getPlayers()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }

        return null;
    }

    public ArcadePlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    public Collection<ArcadePlayer> getPlayers() {
        return this.players.values();
    }

    public SettingsFile getSettings() {
        return this.settings;
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
                new ArcadeCommand(this),
                new GeneralCommands(this),
                new MapCommands(this)
        }) {
            this.registerCommandObject(command);
        }
    }

    private void loadGames() {
        this.games = new GameManager(this);

        // TODO queue
        this.games.cycleNext();
    }

    private void loadMaps() {
        this.maps = new MapManager(this);

        this.maps.setParser(new XMLMapParser.XMLParserTechnology());
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

        this.getLogger().info("Loaded " + moduleList.size() + " modules.");

        for (Module<?> module : moduleList) {
            module.initialize(this);
        }

        this.getModules().getContainer().register(moduleList.toArray(new Module<?>[moduleList.size()]));
    }

    private class ArcadeCommons implements Commons {
        private final Commands commands;
        private final Events events;
        private final Sessions<?> sessions;
        private final Storages storages;

        public ArcadeCommons(ArcadePlugin plugin) {
            this.commands = new pl.themolka.arcade.command.Commands(plugin);
            this.events = new pl.themolka.arcade.event.Events();
            this.sessions = new pl.themolka.arcade.session.Sessions(plugin);
            this.storages = new pl.themolka.arcade.storage.Storages();
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
