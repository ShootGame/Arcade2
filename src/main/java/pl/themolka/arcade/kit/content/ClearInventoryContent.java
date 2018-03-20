package pl.themolka.arcade.kit.content;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
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
import pl.themolka.arcade.xml.XMLParser;

public class ClearInventoryContent implements KitContent<Boolean> {
    private final boolean result;

    public ClearInventoryContent(boolean result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().clearInventory(this.getResult());
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<ClearInventoryContent> {
        @Override
        public ClearInventoryContent parse(Element xml) throws DataConversionException {
            boolean armor = XMLParser.parseBoolean(xml.getAttributeValue("armor"), false);
            return new ClearInventoryContent(armor);
        }
    }

    @NestedParserName({"clear-inventory", "clearinventory", "clear"})
    @Produces(ClearInventoryContent.class)
    public static class ContentParser extends BaseContentParser<ClearInventoryContent>
                                      implements InstallableParser {
        private Parser<Boolean> armorParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.armorParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<ClearInventoryContent> parseNode(Node node, String name, String value) throws ParserException {
            boolean armor = this.armorParser.parse(node.property("armor", "full")).orDefault(false);
            return ParserResult.fine(node, name, value, new ClearInventoryContent(armor));
        }
    }
}
