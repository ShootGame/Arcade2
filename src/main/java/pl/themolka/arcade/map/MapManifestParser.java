package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.Nulls;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Produces(MapManifest.class)
public class MapManifestParser extends NodeParser<MapManifest>
                               implements InstallableParser {
    private Parser<WorldInfo> worldInfoParser;

    private Set<GameModuleParser<?, ?>> moduleParsers;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.worldInfoParser = context.type(WorldInfo.class);

        this.moduleParsers = new LinkedHashSet<>();
        for (Parser<?> parser : context.getContainer().getParsers()) {
            if (parser instanceof GameModuleParser) {
                this.moduleParsers.add((GameModuleParser<?, ?>) parser);
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("full map manifest");
    }

    @Override
    protected Result<MapManifest> parseTree(Node node, String name) throws ParserException {
        WorldInfo world = this.worldInfoParser.parse(node.child("world")).orDefaultNull();

        Set<IGameModuleConfig<?>> modules = new LinkedHashSet<>();
        Node modulesNode = this.getModulesNode(node);
        for (GameModuleParser<?, ?> parser : this.moduleParsers) {
            Node definedNode = parser.define(modulesNode);

            if (definedNode != null) {
                modules.add(parser.parse(definedNode).orFail());
            }
        }

        return Result.fine(node, name, new MapManifest(modules, node, world));
    }

    protected Node getModulesNode(Node root) throws ParserException {
        return Nulls.defaults(this.findJustOne(root, "modules", "module", "components", "component"), root);
    }

    private Node findJustOne(Node parent, String... names) throws ParserException {
        Node result = null;
        for (String name : names) {
            Node child = parent.firstChild(name);
            if (child == null) {
                continue;
            }

            if (result != null) {
                throw this.fail(child, "Modules node is already defined");
            }
            result = child;
        }

        return result;
    }
}
