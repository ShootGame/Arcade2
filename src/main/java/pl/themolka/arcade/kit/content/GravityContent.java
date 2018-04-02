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

public class GravityContent implements RemovableKitContent<Boolean> {
    private final boolean result;

    protected GravityContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        player.getBukkit().setGravity(value);
    }

    @Override
    public Boolean defaultValue() {
        return Config.DEFAULT_GRAVITY;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName("gravity")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> gravityParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.gravityParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Boolean> result() { return Ref.empty(); }
                });
            }

            boolean gravity = this.gravityParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(gravity); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<GravityContent, Boolean> {
        boolean DEFAULT_GRAVITY = true;

        @Override
        default GravityContent create(Game game) {
            return new GravityContent(this);
        }
    }
}
