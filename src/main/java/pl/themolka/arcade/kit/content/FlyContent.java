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

public class FlyContent implements RemovableKitContent<Boolean> {
    private final boolean result;

    public FlyContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        player.getBukkit().setFlying(value);
    }

    @Override
    public Boolean defaultValue() {
        return Config.DEFAULT_FLY;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName({"fly", "flying"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> flyParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.flyParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Boolean> result() { return Ref.empty(); }
                });
            }

            boolean fly = this.flyParser.parse(node).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(fly); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<FlyContent, Boolean> {
        boolean DEFAULT_FLY = false;

        @Override
        default FlyContent create(Game game) {
            return new FlyContent(this);
        }
    }
}
