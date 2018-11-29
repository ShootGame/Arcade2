package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Node;
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

@Produces(GameRule.class)
public class GameRuleParser extends NodeParser<GameRule>
                            implements InstallableParser {
    private Parser<GameRuleType> typeParser;
    private Parser<String> valueParser;
    private Parser<String> keyParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.typeParser = context.type(GameRuleType.class);
        this.valueParser = context.text();
        this.keyParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("game rule");
    }

    @Override
    protected Result<GameRule> parsePrimitive(Node node, String name, String value) throws ParserException {
        GameRuleType type = this.typeParser.parseWithDefinition(node, name, name).orNull(); // name is the value
        String ruleValue = this.valueParser.parseWithDefinition(node, name, value).orFail();

        if (type != null) {
            return Result.fine(node, name, value, type.create(ruleValue));
        }

        String ruleKey = this.keyParser.parseWithValue(node, name).orFail();
        return Result.fine(node, name, value, new GameRule(ruleKey, ruleValue));
    }
}
