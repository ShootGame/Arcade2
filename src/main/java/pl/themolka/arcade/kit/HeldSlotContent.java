package pl.themolka.arcade.kit;

import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class HeldSlotContent extends BaseInventoryContent<Integer> {
    public HeldSlotContent(int result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setHeldItemSlot(this.getResult());
    }

    public static class Parser implements KitContentParser<HeldSlotContent> {
        @Override
        public HeldSlotContent parse(Element xml) throws DataConversionException {
            int slot = XMLParser.parseInt(xml.getValue(), -1);
            return slot < 0 || slot > 8 ? null : new HeldSlotContent(slot);
        }
    }
}
