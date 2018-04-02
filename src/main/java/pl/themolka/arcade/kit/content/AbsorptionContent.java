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

public class AbsorptionContent implements RemovableKitContent<Float> {
    public static final float MIN_VALUE = 0F;

    public static boolean testValue(float value) {
        return value >= MIN_VALUE;
    }

    private final float result;

    protected AbsorptionContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setAbsorption(value);
    }

    @Override
    public Float defaultValue() {
        return Config.DEFAULT_ABSORPTION;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName("absorption")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> absorptionParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.absorptionParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float absorption = this.absorptionParser.parseWithDefinition(node, name, value).orFail();
            if (absorption < MIN_VALUE) {
                throw this.fail(node, name, value, "Absorption cannot be negative (smaller than 0)");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(absorption); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<AbsorptionContent, Float> {
        float DEFAULT_ABSORPTION = 0F;

        @Override
        default AbsorptionContent create(Game game) {
            return new AbsorptionContent(this);
        }
    }
}
