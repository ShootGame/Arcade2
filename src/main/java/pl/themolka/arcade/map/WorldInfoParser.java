package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
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
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.difficultyParser = context.type(Difficulty.class);
        this.environmentParser = context.type(World.Environment.class);
        this.generatorParser = context.type(Generator.class);
        this.hardcoreParser = context.type(Boolean.class);
        this.pvpParser = context.type(Boolean.class);
        this.randomSeedParser = context.type(RandomSeed.class);
        this.spawnParser = context.type(Location.class);
        this.timeParser = context.type(MapTime.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("world information");
    }

    @Override
    protected Result<WorldInfo> parseTree(Node node, String name) throws ParserException {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficultyParser.parse(node.property("difficulty")).orNull());
        info.setEnvironment(this.environmentParser.parse(node.property("environment")).orNull());
        info.setGenerator(this.generatorParser.parse(node.firstChild("generator")).orNull());
        info.setHardcore(this.hardcoreParser.parse(node.property("hardcore")).orDefault(WorldInfo.DEFAULT_IS_HARDCORE));
        info.setPvp(this.pvpParser.parse(node.property("pvp")).orDefault(WorldInfo.DEFAULT_IS_PVP));
        info.setRandomSeed(this.randomSeedParser.parse(node.property("seed", "random-seed")).orDefault(WorldInfo.DEFAULT_RANDOM_SEED));
        info.setSpawn(this.spawnParser.parse(node.firstChild("spawn")).orNull());
        info.setTime(this.timeParser.parse(node.firstChild("time")).orNull());
        return Result.fine(node, name, info);
    }
}
