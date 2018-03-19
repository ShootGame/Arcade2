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
import pl.themolka.arcade.xml.XMLParser;

public class ItemStackContent extends BaseInventoryContent<ItemStack>
                              implements BaseModeContent {
    // armor
    public static final int SLOT_HELMET = 103;
    public static final int SLOT_CHESTPLATE = 102;
    public static final int SLOT_LEGGNINGS = 101;
    public static final int SLOT_BOOTS = 100;

    private final Mode mode;
    private Integer slot;

    public ItemStackContent(ItemStack result) {
        this(result, Mode.GIVE);
    }

    public ItemStackContent(ItemStack result, Mode mode) {
        super(result);

        this.mode = mode;
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        if (this.give()) {
            if (this.hasSlot()) {
                inventory.setItem(this.getSlot(), this.getResult());
            } else {
                inventory.addItem(this.getResult());
            }
        } else if (this.take()) {
            inventory.remove(this.getResult());
        }
    }

    @Override
    public boolean give() {
        return this.mode.equals(Mode.GIVE);
    }

    @Override
    public boolean take() {
        return this.mode.equals(Mode.TAKE);
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasSlot() {
        return this.slot != null;
    }

    public void resetSlot() {
        this.setSlot(null);
    }

    public ItemStackContent setSlot(Integer slot) {
        this.slot = slot;
        return this;
    }

    public static class LegacyParser implements LegacyKitContentParser<ItemStackContent> {
        @Override
        public ItemStackContent parse(Element xml) throws DataConversionException {
            ItemStackContent content = new ItemStackContent(XMLItemStack.parse(xml));

            int slot = XMLParser.parseInt(xml.getAttributeValue("slot"), -1);
            if (slot != -1) {
                content.setSlot(slot);
            }

            return content;
        }
    }

    @NestedParserName({"item", "item-stack", "itemstack"})
    @Produces(ItemStackContent.class)
    public static class ContentParser extends BaseContentParser<ItemStackContent>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;
        private Parser<Mode> modeParser;
        private Parser<Integer> slotParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.itemStackParser = context.type(ItemStack.class);
            this.modeParser = context.type(Mode.class);
            this.slotParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<ItemStackContent> parseNode(Node node, String name, String value) throws ParserException {
            ItemStack itemStack = this.itemStackParser.parse(node).orFail();

            ItemStackContent content = new ItemStackContent(itemStack, this.modeParser.parse(node).orFail());
            content.setSlot(this.slotParser.parse(node.property("slot")).orDefaultNull());
            return ParserResult.fine(node, name, value, content);
        }
    }
}
