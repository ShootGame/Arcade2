package pl.themolka.arcade.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.map.WorldInfo;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class VoidGenerator extends ChunkGenerator implements Generator {
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
        return WorldInfo.DEFAULT_SPAWN.clone();
    }

    @NestedParserName("void")
    @Produces(VoidGenerator.class)
    public static class GeneratorParser extends BaseGeneratorParser<VoidGenerator> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("void world generator");
        }

        @Override
        protected ParserResult<VoidGenerator> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, value, new VoidGenerator());
        }
    }
}
