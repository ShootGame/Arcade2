package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(GameRule.class)
public class GameRuleParser extends NodeParser<GameRule>
                            implements InstallableParser {
    private Parser<GameRuleType> typeParser;

    @Override
    public void install(ParserContext context) {
        this.typeParser = context.type(GameRuleType.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("game rule");
    }

    @Override
    protected ParserResult<GameRule> parsePrimitive(Node node, String name, String value) throws ParserException {
        GameRuleType type = this.typeParser.parseWithDefinition(node, name, name).orFail(); // name is the value
        return ParserResult.fine(node, name, value, new GameRule(type, value));
    }
}
