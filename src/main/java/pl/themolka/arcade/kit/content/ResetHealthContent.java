package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class ResetHealthContent implements BaseVoidKitContent {
    protected ResetHealthContent() {
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.resetHealth();
    }

    @NestedParserName({"reset-health", "resethealth"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config> {
        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends BaseVoidKitContent.Config<ResetHealthContent> {
        @Override
        default ResetHealthContent create(Game game) {
            return new ResetHealthContent();
        }
    }
}
