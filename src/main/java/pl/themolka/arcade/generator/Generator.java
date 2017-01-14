package pl.themolka.arcade.generator;

import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public interface Generator {
    ChunkGenerator getChunkGenerator();

    default WorldType getWorldType() {
        return WorldType.NORMAL;
    }

    default boolean isVanilla() {
        return this.getChunkGenerator() == null;
    }
}
