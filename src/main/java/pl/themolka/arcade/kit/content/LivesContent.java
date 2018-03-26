package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class LivesContent implements RemovableKitContent<Integer> {
    private final int result;

    protected LivesContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        LivesGame module = (LivesGame) player.getGame().getModule(LivesModule.class);
        if (module != null && module.isEnabled()) {
            module.addLives(player, value);
        }
    }

    @Override
    public Integer defaultValue() {
        return Config.DEFAULT_LIVES;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"lives", "life"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> livesParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.livesParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Integer> result() { return Ref.empty(); }
                });
            }

            int lives = this.livesParser.parse(node).orFail();
            if (lives == 0) {
                throw this.fail(node, name, value, "No lives to increment or decrement");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(lives); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<LivesContent, Integer> {
        int DEFAULT_LIVES = +1;

        @Override
        default LivesContent create(Game game) {
            return new LivesContent(this);
        }
    }
}
