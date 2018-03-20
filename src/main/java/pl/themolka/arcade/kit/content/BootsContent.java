package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class BootsContent extends BaseArmorContent {
    public BootsContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setBoots(this.getResult());
    }

    public static class LegacyParser implements LegacyKitContentParser<BootsContent> {
        @Override
        public BootsContent parse(Element xml) throws DataConversionException {
            return new BootsContent(XMLItemStack.parse(xml));
        }
    }

    @NestedParserName({"boots", "armor-boots", "armorboots"})
    @Produces(BootsContent.class)
    public static class ContentParser extends BaseRemovableContentParser<BootsContent>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.itemStackParser = context.type(ItemStack.class);
        }

        @Override
        protected ParserResult<BootsContent> parseNode(Node node, String name, String value) throws ParserException {
            ItemStack boots = this.reset(node) ? null : this.itemStackParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new BootsContent(boots));
        }
    }
}
