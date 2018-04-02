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

public class WalkSpeedContent implements RemovableKitContent<Float> {
    public static final float MIN_VALUE = -1F;
    public static final float MAX_VALUE = +1F;

    public static boolean testValue(float value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    private final float result;

    protected WalkSpeedContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setWalkSpeed(value);
    }

    @Override
    public Float defaultValue() {
        return Config.DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName({"walk-speed", "walkspeed"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> speedParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.speedParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float speed = this.speedParser.parseWithDefinition(node, name,  value).orFail();
            if (speed < MIN_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too slow (min " + MIN_VALUE + ")");
            } else if (speed > MAX_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too fast (max " + MAX_VALUE + ")");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(speed); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<WalkSpeedContent, Float> {
        float DEFAULT_SPEED = 0.2F;

        @Override
        default WalkSpeedContent create(Game game) {
            return new WalkSpeedContent(this);
        }
    }
}
