package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(GameRuleType.class)
public class GameRuleTypeParser extends ElementParser<GameRuleType> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("game rule type");
    }

    @Override
    protected Result<GameRuleType> parseElement(Element element, String name, String value) throws ParserException {
        GameRuleType type = GameRuleType.byKey(value);
        if (type == null) {
            throw this.fail(element, name, value, "Unknown game rule type");
        }

        return Result.fine(element, name, value, type);
    }
}
