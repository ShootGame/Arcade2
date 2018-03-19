package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class LeggingsContent extends BaseArmorContent {
    public LeggingsContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setLeggings(this.getResult());
    }

    public static class LegacyParser implements LegacyKitContentParser<LeggingsContent> {
        @Override
        public LeggingsContent parse(Element xml) throws DataConversionException {
            return new LeggingsContent(XMLItemStack.parse(xml));
        }
    }

    @NestedParserName({"leggings", "armor-leggings", "armorleggings"})
    @Produces(LeggingsContent.class)
    public static class ContentParser extends BaseRemovableContentParser<LeggingsContent> {
        private Parser<ItemStack> itemStackParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.itemStackParser = context.type(ItemStack.class);
        }

        @Override
        protected ParserResult<LeggingsContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            ItemStack leggings = this.reset(node) ? null : this.itemStackParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new LeggingsContent(leggings));
        }
    }
}
