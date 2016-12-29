package pl.themolka.arcade.map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.generator.VoidGenerator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MapManager implements FilenameFilter {
    public static final String[] FILENAMES = {"level.dat", "region", "data"};

    private final ArcadePlugin plugin;

    private final MapContainer container = new MapContainer();
    private List<MapContainerLoader> loaderList = new ArrayList<>();
    private MapParser.Technology parser;

    public MapManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (name.toLowerCase().endsWith(".xml")) {
            return true;
        }

        for (String filename : FILENAMES) {
            if (name.equals(filename)) {
                return true;
            }
        }

        return false;
    }

    public void addMapLoader(MapContainerLoader loader) {
        this.loaderList.add(loader);
    }

    public File[] copyFiles(ArcadeMap map) throws IOException {
        return this.copyFiles(map, new File(this.getWorldContainer(), map.getWorldName()));
    }

    public File[] copyFiles(ArcadeMap map, File destination) throws IOException {
        return this.copyFiles(map.getMapInfo().getDirectory(), destination);
    }

    public File[] copyFiles(File map, File destination) throws IOException {
        if (!map.exists()) {
            throw new IOException("the map directory doesn't exists");
        } else if (!map.isDirectory()) {
            throw new IOException("the map is not a directory");
        } else if (destination.exists()) {
            this.plugin.getLogger().warning("Deleting old map directory '" + destination.getName() + "'!");
            FileUtils.deleteQuietly(destination);
        }

        List<File> copied = new ArrayList<>();
        for (File from : map.listFiles(this)) {
            File to = new File(destination, from.getName());
            if (from.isDirectory()) {
                FileUtils.copyDirectory(from, to);
            } else {
                FileUtils.copyFile(from, to);
            }

            copied.add(to);
        }

        return copied.toArray(new File[copied.size()]);
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
