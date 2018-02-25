package pl.themolka.arcade.generator;

import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Propertable;

public class VanillaGenerator implements Generator {
    private VanillaGenerator() {
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return null;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.NORMAL;
    }

    public static class Parser implements GeneratorCreator<VanillaGenerator> {
        @Override
        public VanillaGenerator create(ArcadePlugin plugin, Propertable properties) {
            return new VanillaGenerator();
        }
    }
}
