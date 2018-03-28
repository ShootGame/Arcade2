package pl.themolka.arcade.kit.content;

import org.bukkit.attribute.Attribute;
import pl.themolka.arcade.attribute.AttributeKey;
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

public class MaxHealthContent implements RemovableKitContent<Double> {
    public static final AttributeKey MAX_HEALTH = AttributeKey.bukkit(Attribute.GENERIC_MAX_HEALTH);

    public static final double MIN_VALUE = 0D;

    public static boolean testValue(double value) {
        return value > MIN_VALUE;
    }

    private final double result;

    protected MaxHealthContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        if (value != null) {
            player.getAttribute(MAX_HEALTH).setValue(value);
        } else {
            player.getAttribute(MAX_HEALTH).resetValue();
        }
    }

    @Override
    public Double defaultValue() {
        return null;
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @NestedParserName({"max-health", "maxhealth"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Double> maxHealthParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.maxHealthParser = context.type(Double.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Double> result() { return Ref.empty(); }
                });
            }

            double maxHealth = this.maxHealthParser.parse(node).orFail();
            if (maxHealth <= MIN_VALUE) {
                throw this.fail(node, name, value, "Max health must be positive (greater than 0)");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Double> result() { return Ref.ofProvided(maxHealth); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<MaxHealthContent, Double> {
        double DEFAULT_MAX_HEALTH = 20D;

        @Override
        default MaxHealthContent create(Game game) {
            return new MaxHealthContent(this);
        }
    }
}
