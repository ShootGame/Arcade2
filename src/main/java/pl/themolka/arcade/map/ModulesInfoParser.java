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
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Produces(ModulesInfo.class)
public class ModulesInfoParser extends NodeParser<ModulesInfo>
                               implements InstallableParser {
    private Set<GameModuleParser> moduleParsers;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.moduleParsers = new LinkedHashSet<>();

        // fetch installed GameModuleParsers
        for (Parser<?> parser : context.getContainer().getParsers()) {
            if (parser instanceof GameModuleParser) {
                this.moduleParsers.add((GameModuleParser) parser);
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("modules information");
    }

    @Override
    protected ParserResult<ModulesInfo> parseTree(Node node, String name) throws ParserException {
        List<IGameModuleConfig<?>> configs = new ArrayList<>();
        for (GameModuleParser<?, ?> parser : this.moduleParsers) {
            Node definedNode = parser.define(node);

            if (definedNode != null) {
                configs.add(parser.parse(definedNode).orFail());
            }
        }

        ModulesInfo info = new ModulesInfo();
        info.setConfigs(configs);
        return ParserResult.fine(node, name, info);
    }
}
