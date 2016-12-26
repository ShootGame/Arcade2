package pl.themolka.arcade.map;

import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.generator.VoidGenerator;

import java.io.File;
import java.io.IOException;

public class MapManager {
    private final ArcadePlugin plugin;

    private final MapContainer container = new MapContainer();

    public MapManager(ArcadePlugin plugin) {
        this.plugin = plugin;
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
                .type(WorldType.FLAT);

        World world = creator.createWorld();
        world.setAutoSave(false);
        world.setDifficulty(this.plugin.getServer().getWorlds().get(0).getDifficulty());
        world.setPVP(map.isPvp());

        map.setWorld(world);
        return world;
    }

    public MapContainer getContainer() {
        return this.container;
    }

    public VoidGenerator getGenerator() {
        return this.plugin.getGenerator();
    }

    public File getWorldContainer() {
        return this.plugin.getServer().getWorldContainer();
    }
}
