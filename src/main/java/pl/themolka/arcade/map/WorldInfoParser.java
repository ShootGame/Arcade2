package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.GeneratorParser;
import pl.themolka.arcade.generator.GeneratorType;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.type.BooleanParser;
import pl.themolka.arcade.parser.type.DifficultyParser;
import pl.themolka.arcade.parser.type.LocationParser;

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

    private BooleanParser booleanParser;
    private DifficultyParser difficultyParser;
    private EnumParser<World.Environment> environmentParser;
    private GeneratorParser generatorParser;
    private LocationParser locationParser;
    private LongParser longParser;
    private MapTimeParser timeParser;

    @Override
    public void install(ParserContext context) {
        this.booleanParser = context.of(BooleanParser.class);
        this.difficultyParser = context.of(DifficultyParser.class);
        this.environmentParser = context.enumType(World.Environment.class);
        this.generatorParser = context.of(GeneratorParser.class);
        this.locationParser = context.of(LocationParser.class);
        this.longParser = context.of(LongParser.class);
        this.timeParser = context.of(MapTimeParser.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("world information");
    }

    @Override
    protected ParserResult<WorldInfo> parseTree(Node node, String name, String value) throws ParserException {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficultyParser.parse(node.property("difficulty")).orDefault(DEFAULT_DIFFICULTY));
        info.setEnvironment(this.environmentParser.parse(node.property("environment")).orDefault(DEFAULT_ENVIRONMENT));
        info.setGenerator(this.generatorParser.parse(node.firstChild("generator")).orDefault(DEFAULT_GENERATOR));
        info.setPvp(this.booleanParser.parse(node.property("pvp")).orDefault(DEFAULT_IS_PVP));
        info.setRandomSeed(this.longParser.parse(node.property("seed", "random-seed")).orDefault(DEFAULT_RANDOM_SEED));
        info.setSpawn(this.locationParser.parse(node.firstChild("spawn")).orDefault(DEFAULT_SPAWN));
        info.setTime(this.timeParser.parse(node.firstChild("time")).orDefault(DEFAULT_TIME));
        return ParserResult.fine(node, name, value, info);
    }
}
