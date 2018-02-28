package pl.themolka.arcade.map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.generator.Generator;

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
    private Field containerField;
    private List<MapContainerLoader> loaderList = new ArrayList<>();

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
        WorldInfo info = map.getManifest().getWorld();
        Generator generator = info.getGenerator();
        Location spawn = info.getSpawn();

        WorldCreator creator = new WorldCreator(map.getWorldName())
                .environment(info.getEnvironment())
                .generateStructures(false)
                .generator(generator.getChunkGenerator())
                .hardcore(false)
                .seed(info.getRandomSeed().getSeed())
                .type(generator.getWorldType());

        World world = creator.createWorld();
        world.setAutoSave(false);
        world.setDifficulty(info.getDifficulty());
        world.setKeepSpawnInMemory(false);
        world.setPVP(info.isPvp());
        world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

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

    public List<MapContainerLoader> getLoaderList() {
        return this.loaderList;
    }

    public File getWorldContainer() {
        return this.plugin.getServer().getWorldContainer();
    }

    public void setWorldContainer(File container) {
        Server server = this.plugin.getServer();

        try {
            if (this.containerField == null) {
                this.containerField = server.getClass().getDeclaredField("container");
                this.containerField.setAccessible(true);
            }

            this.containerField.set(server, container);
        } catch (ReflectiveOperationException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not set world container", ex);
        }
    }
}
