package pl.themolka.arcade.map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.generator.VoidGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MapManager {
    private final ArcadePlugin plugin;

    private final MapContainer container = new MapContainer();
    private List<MapContainerLoader> loaderList = new ArrayList<>();
    private MapParser.Technology parser;

    public MapManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public void addMapLoader(MapContainerLoader loader) {
        this.loaderList.add(loader);
    }

    public void copyFiles(OfflineMap map) throws IOException {
        this.copyFiles(map.getDirectory());
    }

    public void copyFiles(OfflineMap map, File destination) throws IOException {
        this.copyFiles(map.getDirectory(), destination);
    }

    public void copyFiles(File map) throws IOException {
        this.copyFiles(map, this.getWorldContainer());
    }

    public void copyFiles(File map, File destination) throws IOException {
        String[] filenames = {"level.dat", "region", "data"};

        if (!map.exists()) {
            throw new RuntimeException("the map directory doesn't exists");
        } else if (destination.exists()) {
            FileUtils.deleteQuietly(destination);
        }

        for (String filename : filenames) {
            File from = new File(map, filename);
            if (!from.exists()) {
                continue;
            }

            if (from.isDirectory()) {
                FileUtils.copyDirectory(from, new File(destination, filename));
            } else {
                FileUtils.copyFile(from, new File(destination, filename));
            }
        }
    }

    public World createWorld(ArcadeMap map) {
        WorldCreator creator = new WorldCreator(map.getWorldName())
                .environment(map.getEnvironment())
                .generateStructures(false)
                .generator(this.getGenerator())
                .hardcore(false)
                .type(WorldType.FLAT);

        World world = creator.createWorld();
        world.setAutoSave(false);
        world.setDifficulty(this.plugin.getServer().getWorlds().get(0).getDifficulty());
        world.setPVP(map.isPvp());
        world.setSpawnFlags(false, false);
        world.setSpawnLocation(map.getSpawn().getBlockX(), map.getSpawn().getBlockY(), map.getSpawn().getBlockZ());

        map.setWorld(world);
        return world;
    }

    public void destroyWorld(World world) {
        this.destroyWorld(world, false);
    }

    public void destroyWorld(World world, boolean save) {
        this.plugin.getServer().unloadWorld(world, save);
    }

    public List<OfflineMap> findMap(String query) {
        return this.getContainer().findMap(query);
    }

    public OfflineMap findMapFirst(String query) {
        return this.getContainer().findMapFirst(query);
    }

    public MapContainer getContainer() {
        return this.container;
    }

    public MapContainer getLoaderListContainer() {
        MapContainer container = new MapContainer();
        for (MapContainerLoader loader : this.getLoaderList()) {
            try {
                container.register(loader.loadContainer());
            } catch (Throwable ignored) {
            }
        }

        return container;
    }

    public VoidGenerator getGenerator() {
        return this.plugin.getGenerator();
    }

    public List<MapContainerLoader> getLoaderList() {
        return this.loaderList;
    }

    public MapParser.Technology getParser() {
        return this.parser;
    }

    public File getWorldContainer() {
        return this.plugin.getServer().getWorldContainer();
    }

    public void setParser(MapParser.Technology parser) {
        this.parser = parser;
    }

    public void setWorldContainer(File container) {
        Server server = this.plugin.getServer();

        try {
            Field field = server.getClass().getDeclaredField("container");
            field.setAccessible(true);
            field.set(server, container);
        } catch (ReflectiveOperationException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not set world container", ex);
        }
    }
}