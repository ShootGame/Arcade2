package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.Percentage;

public class ChanceMatcher extends ConfigurableMatcher<Percentage> {
    protected ChanceMatcher(Config config) {
        super(config.value().get().trim());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Percentage) {
            return this.matches((Percentage) object);
        } else if (object instanceof Double) {
            return this.matches((Double) object);
        }

        return false;
    }

    @Override
    public boolean matches(Percentage percentage) {
        return percentage != null && this.getValue().getValue() >= percentage.getValue();
    }

    public boolean matches(Double number) {
        return number != null && this.matches(Percentage.finite(number.doubleValue()));
    }

    @NestedParserName({"chance", "random"})
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<Percentage> chanceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.chanceParser = context.type(Percentage.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            Percentage chance = this.chanceParser.parseWithDefinition(node, name, value).orFail();
            if (!chance.isNormalized()) {
                throw this.fail(node, name, value, "Chance must be normalized");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Percentage> value() { return Ref.ofProvided(chance); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<ChanceMatcher, Percentage> {
        @Override
        default ChanceMatcher create(Game game) {
            return new ChanceMatcher(this);
        }
    }
}
