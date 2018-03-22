package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
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

    public TitleContent(PlayerTitle result) {
        this.result = result;
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
        return null;
    }

    @Override
    public PlayerTitle getResult() {
        return this.result;
    }

    @NestedParserName("title")
    @Produces(TitleContent.class)
    public static class ContentParser extends BaseRemovableContentParser<TitleContent>
                                      implements InstallableParser {
        private Parser<PlayerTitle> titleParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.titleParser = context.type(PlayerTitle.class);
        }

        @Override
        protected ParserResult<TitleContent> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, value, new TitleContent(this.titleParser.parse(node).orFail()));
        }
    }
}
