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
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class HelmetContent extends BaseArmorContent {
    public HelmetContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setHelmet(this.getResult());
    }

    public static class LegacyParser implements LegacyKitContentParser<HelmetContent> {
        @Override
        public HelmetContent parse(Element xml) throws DataConversionException {
            return new HelmetContent(XMLItemStack.parse(xml));
        }
    }

    @NestedParserName({"helmet", "armor-helmet", "armorhelmet"})
    @Produces(HelmetContent.class)
    public static class ContentParser extends BaseRemovableContentParser<HelmetContent> {
        private Parser<ItemStack> itemStackParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.itemStackParser = context.type(ItemStack.class);
        }

        @Override
        protected ParserResult<HelmetContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            ItemStack helmet = this.reset(node) ? null : this.itemStackParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new HelmetContent(helmet));
        }
    }
}
