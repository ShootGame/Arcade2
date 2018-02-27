package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.GeneratorType;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(WorldInfo.class)
public class WorldInfoParser extends NodeParser<WorldInfo>
                             implements InstallableParser {
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    public static final World.Environment DEFAULT_ENVIRONMENT = World.Environment.NORMAL;
    public static final Generator DEFAULT_GENERATOR = GeneratorType.getDefaultGenerator();
    public static final boolean DEFAULT_IS_PVP = true;
    public static final long DEFAULT_RANDOM_SEED = 0L;
    public static final Location DEFAULT_SPAWN = new Location((World) null, 0.5D, 16D, 0.5D);
    public static final MapTime DEFAULT_TIME = MapTime.defaultTime();

    private Parser<Boolean> booleanParser;
    private Parser<Difficulty> difficultyParser;
    private Parser<World.Environment> environmentParser;
    private Parser<Generator> generatorParser;
    private Parser<Location> locationParser;
    private Parser<Long> longParser;
    private Parser<MapTime> timeParser;

    @Override
    public void install(ParserContext context) {
        this.booleanParser = context.type(Boolean.class);
        this.difficultyParser = context.type(Difficulty.class);
        this.environmentParser = context.enumType(World.Environment.class);
        this.generatorParser = context.type(Generator.class);
        this.locationParser = context.type(Location.class);
        this.longParser = context.type(Long.class);
        this.timeParser = context.type(MapTime.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("world information");
    }

    @Override
    protected ParserResult<WorldInfo> parseTree(Node node, String name) throws ParserException {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficultyParser.parse(node.property("difficulty")).orDefault(DEFAULT_DIFFICULTY));
        info.setEnvironment(this.environmentParser.parse(node.property("environment")).orDefault(DEFAULT_ENVIRONMENT));
        info.setGenerator(this.generatorParser.parse(node.firstChild("generator")).orDefault(DEFAULT_GENERATOR));
        info.setPvp(this.booleanParser.parse(node.property("pvp")).orDefault(DEFAULT_IS_PVP));
        info.setRandomSeed(this.longParser.parse(node.property("seed", "random-seed")).orDefault(DEFAULT_RANDOM_SEED));
        info.setSpawn(this.locationParser.parse(node.firstChild("spawn")).orDefault(DEFAULT_SPAWN));
        info.setTime(this.timeParser.parse(node.firstChild("time")).orDefault(DEFAULT_TIME));
        return ParserResult.fine(node, name, info);
    }
}
