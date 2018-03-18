package pl.themolka.arcade.generator;

import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

public class VanillaGenerator implements Generator {
    @Override
    public ChunkGenerator getChunkGenerator() {
        return null;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.NORMAL;
    }

    @NestedParserName("vanilla")
    @Produces(VanillaGenerator.class)
    public static class GeneratorParser extends BaseGeneratorParser<VanillaGenerator> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("vanilla world generator");
        }

        @Override
        protected ParserResult<VanillaGenerator> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, value, new VanillaGenerator());
        }
    }
}
