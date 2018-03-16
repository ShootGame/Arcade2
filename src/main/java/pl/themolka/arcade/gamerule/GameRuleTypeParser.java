package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(GameRuleType.class)
public class GameRuleTypeParser extends ElementParser<GameRuleType> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("game rule type");
    }

    @Override
    protected ParserResult<GameRuleType> parseElement(Element element, String name, String value) throws ParserException {
        GameRuleType type = GameRuleType.byKey(value);
        if (type == null) {
            throw this.fail(element, name, value, "Unknown game rule type");
        }

        return ParserResult.fine(element, name, value, type);
    }
}
