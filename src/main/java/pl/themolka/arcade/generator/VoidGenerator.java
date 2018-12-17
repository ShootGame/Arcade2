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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.map.WorldInfo;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

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
        protected Result<VoidGenerator> parseNode(Context context, Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new VoidGenerator());
        }
    }
}
