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
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class HealthScaleContent implements RemovableKitContent<Double> {
    public static final double MIN_VALUE = 0.0;

    public static boolean testValue(double value) {
        return value > MIN_VALUE;
    }

    private final double result;

    protected HealthScaleContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        player.getBukkit().setHealthScale(value);
    }

    @Override
    public Double defaultValue() {
        return Config.DEFAULT_SCALE;
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @NestedParserName({"health-scale", "healthscale"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Double> scaleParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.scaleParser = context.type(Double.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Double> result() { return Ref.empty(); }
                });
            }

            double scale = this.scaleParser.parseWithDefinition(node, name, value).orFail();
            if (scale <= MIN_VALUE) {
                throw this.fail(node, name, value, "Health scale must be positive (greater than 0)");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Double> result() { return Ref.ofProvided(scale); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<HealthScaleContent, Double> {
        double DEFAULT_SCALE = 20L;

        @Override
        default HealthScaleContent create(Game game) {
            return new HealthScaleContent(this);
        }
    }
}
