package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class KillContent implements BaseVoidKitContent {
    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        player.kill();
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<KillContent> {
        @Override
        public KillContent parse(Element xml) throws DataConversionException {
            return new KillContent();
        }
    }

    @NestedParserName("name")
    @Produces(KillContent.class)
    public static class ContentParser extends BaseContentParser<KillContent> {
        @Override
        protected ParserResult<KillContent> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, value, new KillContent());
        }
    }
}
