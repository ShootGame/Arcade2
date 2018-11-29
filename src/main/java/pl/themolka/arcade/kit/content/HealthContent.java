package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class HealthContent implements RemovableKitContent<Double> {
    public static final double MIN_VALUE = 0D;

    public static boolean testValue(double value) {
        return value >= MIN_VALUE;
    }

    private final double result;

    protected HealthContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        player.getBukkit().setHealth(value);
    }

    @Override
    public Double defaultValue() {
        return Config.DEFAULT_HEALTH;
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @NestedParserName("health")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Double> healthParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.healthParser = context.type(Double.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Double> result() { return Ref.empty(); }
                });
            }

            double health = this.healthParser.parseWithDefinition(node, name, value).orFail();
            if (health < MIN_VALUE) {
                throw this.fail(node, name, value, "Health cannot be negative (smaller than 0)");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Double> result() { return Ref.ofProvided(health); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<HealthContent, Double> {
        double DEFAULT_HEALTH = 20D;

        @Override
        default HealthContent create(Game game, Library library) {
            return new HealthContent(this);
        }
    }
}
