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

public class SaturationContent implements RemovableKitContent<Float> {
    private final float result;

    protected SaturationContent(Config config) {
        this.result = config.result().getOrDefault(Config.DEFAULT_SATURATION);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setSaturation(value);
    }

    @Override
    public Float defaultValue() {
        return Config.DEFAULT_SATURATION;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName("saturation")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> saturationParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.saturationParser = context.type(Float.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float saturation = this.saturationParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(saturation); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<SaturationContent, Float> {
        float DEFAULT_SATURATION = 5F;

        @Override
        default SaturationContent create(Game game, Library library) {
            return new SaturationContent(this);
        }
    }
}
