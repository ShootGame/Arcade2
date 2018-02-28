package pl.themolka.arcade.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Propertable;
import pl.themolka.arcade.map.WorldInfo;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator implements Generator {
    private VoidGenerator() {
    }

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return new byte[16 * 256 * 16]; // generate an empty world without any blocks
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
        return WorldInfo.DEFAULT_SPAWN;
    }

    public static class Parser implements GeneratorCreator<VoidGenerator> {
        @Override
        public VoidGenerator create(ArcadePlugin plugin, Propertable properties) {
            return new VoidGenerator();
        }
    }
}
