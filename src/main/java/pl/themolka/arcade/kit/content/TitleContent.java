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
import pl.themolka.arcade.session.PlayerTitle;

public class TitleContent implements RemovableKitContent<PlayerTitle> {
    private final PlayerTitle result;

    protected TitleContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, PlayerTitle value) {
        if (value != null) {
            value.apply(player);
        } else {
            player.getBukkit().hideTitle();
        }
    }

    @Override
    public PlayerTitle defaultValue() {
        return Config.DEFAULT_TITLE;
    }

    @Override
    public PlayerTitle getResult() {
        return this.result;
    }

    @NestedParserName("title")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<PlayerTitle> titleParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.titleParser = context.type(PlayerTitle.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<PlayerTitle> result() { return Ref.empty(); }
                });
            }

            PlayerTitle title = this.titleParser.parse(node).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<PlayerTitle> result() { return Ref.ofProvided(title); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<TitleContent, PlayerTitle> {
        PlayerTitle DEFAULT_TITLE = null;

        @Override
        default TitleContent create(Game game) {
            return new TitleContent(this);
        }
    }
}
