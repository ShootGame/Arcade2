package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(MapManifest.class)
public class MapManifestParser extends NodeParser<MapManifest>
                               implements InstallableParser {
    private Parser<WorldInfo> worldInfoParser;
    private Parser<ScoreboardInfo> scoreboardInfoParser;
    private Parser<ModulesInfo> modulesInfoParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.worldInfoParser = context.type(WorldInfo.class);
        this.scoreboardInfoParser = context.type(ScoreboardInfo.class);
        this.modulesInfoParser = context.type(ModulesInfo.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("full map manifest");
    }

    @Override
    protected ParserResult<MapManifest> parseTree(Node node, String name) throws ParserException {
        WorldInfo world = this.worldInfoParser.parse(node.child("world")).orDefaultNull();
        ScoreboardInfo scoreboard = this.scoreboardInfoParser.parse(node.child("scoreboard")).orDefaultNull();
        ModulesInfo modules = this.modulesInfoParser.parse(node.child("modules")).orDefaultNull();
        return ParserResult.fine(node, name, new MapManifest(modules, scoreboard, node, world));
    }
}
