/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.generator;

import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class VanillaGenerator implements Generator {
    private final WorldType worldType;

    public VanillaGenerator(WorldType worldType) {
        this.worldType = Objects.requireNonNull(worldType, "worldType cannot be null");
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return null;
    }

    @Override
    public WorldType getWorldType() {
        return this.worldType;
    }

    @NestedParserName("vanilla")
    @Produces(VanillaGenerator.class)
    public static class GeneratorParser extends BaseGeneratorParser<VanillaGenerator>
                                        implements InstallableParser {
        private Parser<WorldType> worldTypeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.worldTypeParser = context.type(WorldType.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton("vanilla world generator");
        }

        @Override
        protected Result<VanillaGenerator> parseNode(Node node, String name, String value) throws ParserException {
            WorldType worldType = this.worldTypeParser.parse(node.property("world-type", "worldtype")).orDefault(WorldType.NORMAL);

            return Result.fine(node, name, value, new VanillaGenerator(worldType));
        }
    }
}
