package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.NoneCondition;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Set;

public class NoneOperator extends Operator {
    protected NoneOperator(Game game, Config config) {
        super(game, config);
    }

    @Override
    public AbstainableResult filter(Object... objects) {
        return new NoneCondition(this.getBody()).query(objects);
    }

    @NestedParserName("none")
    @Produces(Config.class)
    public static class OperatorParser extends BaseOperatorParser<Config> {
        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            Set<Filter.Config<?>> body = this.parseBody(node, name, value);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Set<Filter.Config<?>>> body() { return Ref.ofProvided(body); }
            });
        }
    }

    public interface Config extends Operator.Config<NoneOperator> {
        @Override
        default NoneOperator create(Game game) {
            return new NoneOperator(game, this);
        }
    }
}
