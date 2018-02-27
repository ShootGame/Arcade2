package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(ModulesInfo.class)
public class ModulesInfoParser extends NodeParser<ModulesInfo> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("modules info");
    }

    @Override
    protected ParserResult<ModulesInfo> parseTree(Node node, String name) throws ParserException {
        ModulesInfo info = new ModulesInfo();
        info.setModules(node.children());
        return ParserResult.fine(node, name, info);
    }
}
