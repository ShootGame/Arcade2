package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(ArcadeMap.class)
public class MapManifestParser extends NodeParser<ArcadeMap>
                               implements InstallableParser {
    public static final WorldInfo DEFAULT_WORLD_INFO = new WorldInfo();
    public static final ScoreboardInfo DEFAULT_SCOREBOARD_INFO = new ScoreboardInfo();
    public static final ModulesInfo DEFAULT_MODULES_INFO = new ModulesInfo();

    private Parser<WorldInfo> worldInfoParser;
    private Parser<ScoreboardInfo> scoreboardInfoParser;
    private Parser<ModulesInfo> modulesInfoParser;

    @Override
    public void install(ParserContext context) {
        this.worldInfoParser = context.type(WorldInfo.class);
        this.scoreboardInfoParser = context.type(ScoreboardInfo.class);
        this.modulesInfoParser = context.type(ModulesInfo.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("full map manifest");
    }

    @Override
    protected ParserResult<ArcadeMap> parseTree(Node node, String name) throws ParserException {
        WorldInfo worldInfo = this.worldInfoParser.parse(node.child("world")).orDefault(DEFAULT_WORLD_INFO);
        ScoreboardInfo scoreboardInfo = this.scoreboardInfoParser.parse(node.child("scoreboard")).orDefault(DEFAULT_SCOREBOARD_INFO);
        ModulesInfo modulesInfo = this.modulesInfoParser.parse(node.child("modules")).orDefault(DEFAULT_MODULES_INFO);
        throw this.fail(node, name, null, "Work in Progress!");
        // TODO: return ArcadeMap
    }
}
