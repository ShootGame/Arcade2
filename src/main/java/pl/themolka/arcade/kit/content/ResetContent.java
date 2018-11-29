package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class ResetContent implements BaseVoidKitContent {
    protected ResetContent() {
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.reset();
    }

    @NestedParserName({"reset", "hard-reset", "hardreset"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config> {
        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends BaseVoidKitContent.Config<ResetContent> {
        @Override
        default ResetContent create(Game game, Library library) {
            return new ResetContent();
        }
    }
}
