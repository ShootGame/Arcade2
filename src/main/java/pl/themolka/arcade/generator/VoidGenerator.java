package pl.themolka.arcade.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Properties;
import java.util.Random;

public class VoidGenerator extends pl.themolka.commons.generator.VoidGenerator implements Generator {
    private VoidGenerator() {
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return this;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.FLAT;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.5D, 16D, 0.5D);
    }

    public static class Parser implements GeneratorCreator<VoidGenerator> {
        @Override
        public VoidGenerator create(ArcadePlugin plugin, ArcadeMap map, Properties properties) {
            return new VoidGenerator();
        }
    }
}
