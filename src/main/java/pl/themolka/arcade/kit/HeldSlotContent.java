package pl.themolka.arcade.kit;

import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.xml.XMLParser;

public class HeldSlotContent extends BaseInventoryContent<Integer> {
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = net.minecraft.server.PlayerInventory.getHotbarSize() - 1;

    public static boolean testValue(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    public HeldSlotContent(int result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setHeldItemSlot(this.getResult());
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<HeldSlotContent> {
        @Override
        public HeldSlotContent parse(Element xml) throws DataConversionException {
            int slot = XMLParser.parseInt(xml.getValue(), -1);
            return slot < 0 || slot > 8 ? null : new HeldSlotContent(slot);
        }
    }

    @NestedParserName({"held-slot", "heldslot", "held-item", "helditem", "held", "slot"})
    @Produces(HeldSlotContent.class)
    public static class ContentParser extends BaseContentParser<HeldSlotContent>
                                      implements InstallableParser {
        private Parser<Integer> slotParser;

        @Override
        public void install(ParserContext context) {
            this.slotParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<HeldSlotContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            int slot = this.slotParser.parse(node).orFail();
            if (slot < MIN_VALUE || slot > MAX_VALUE) {
                throw this.fail(node, name, value, "Unknown slot number (not in " + MIN_VALUE + "-" + MAX_VALUE + " range)");
            }

            return ParserResult.fine(node, name, value, new HeldSlotContent(slot));
        }
    }
}
