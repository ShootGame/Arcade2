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

public class FallDistanceContent implements RemovableKitContent<Float> {
    public static final float MIN_VALUE = 0F;

    public static boolean testValue(float value) {
        return value >= MIN_VALUE;
    }

    private final float result;

    protected FallDistanceContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setFallDistance(value);
    }

    @Override
    public Float defaultValue() {
        return Config.DEFAULT_DISTANCE;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName({"fall-distance", "falldistance"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> distanceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.distanceParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float distance = this.distanceParser.parseWithDefinition(node, name, value).orFail();
            if (distance < MIN_VALUE) {
                throw this.fail(node, name, value, "Fall distance cannot be negative (smaller than 0)");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(distance); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<FallDistanceContent, Float> {
        float DEFAULT_DISTANCE = 0F;

        @Override
        default FallDistanceContent create(Game game, Library library) {
            return new FallDistanceContent(this);
        }
    }
}
