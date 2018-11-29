package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class RemoveArrowsContent implements BaseVoidKitContent {
    public static final int FINAL_COUNT = 0;

    protected RemoveArrowsContent() {
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setArrowsStuck(FINAL_COUNT);
    }

    @NestedParserName({"remove-arrows", "removearrows"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config> {
        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends BaseVoidKitContent.Config<RemoveArrowsContent> {
        @Override
        default RemoveArrowsContent create(Game game, Library library) {
            return new RemoveArrowsContent();
        }
    }
}
