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

package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(WorldInfo.class)
public class WorldInfoParser extends NodeParser<WorldInfo>
                             implements InstallableParser {
    private Parser<Difficulty> difficultyParser;
    private Parser<World.Environment> environmentParser;
    private Parser<Generator> generatorParser;
    private Parser<Boolean> hardcoreParser;
    private Parser<Boolean> pvpParser;
    private Parser<RandomSeed> randomSeedParser;
    private Parser<Location> spawnParser;
    private Parser<MapTime> timeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.difficultyParser = library.type(Difficulty.class);
        this.environmentParser = library.type(World.Environment.class);
        this.generatorParser = library.type(Generator.class);
        this.hardcoreParser = library.type(Boolean.class);
        this.pvpParser = library.type(Boolean.class);
        this.randomSeedParser = library.type(RandomSeed.class);
        this.spawnParser = library.type(Location.class);
        this.timeParser = library.type(MapTime.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("world information");
    }

    @Override
    protected Result<WorldInfo> parseTree(Context context, Node node, String name) throws ParserException {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficultyParser.parse(context, node.property("difficulty")).orNull());
        info.setEnvironment(this.environmentParser.parse(context, node.property("environment")).orNull());
        info.setGenerator(this.generatorParser.parse(context, node.firstChild("generator")).orNull());
        info.setHardcore(this.hardcoreParser.parse(context, node.property("hardcore")).orDefault(WorldInfo.DEFAULT_IS_HARDCORE));
        info.setPvp(this.pvpParser.parse(context, node.property("pvp")).orDefault(WorldInfo.DEFAULT_IS_PVP));
        info.setRandomSeed(this.randomSeedParser.parse(context, node.property("seed", "random-seed")).orDefault(WorldInfo.DEFAULT_RANDOM_SEED));
        info.setSpawn(this.spawnParser.parse(context, node.firstChild("spawn")).orNull());
        info.setTime(this.timeParser.parse(context, node.firstChild("time")).orNull());
        return Result.fine(node, name, info);
    }
}
