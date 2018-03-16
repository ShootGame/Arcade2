package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;
import pl.themolka.arcade.xml.XMLParser;

public class ItemStackContent extends BaseInventoryContent<ItemStack> implements Removable {
    public static final int SLOT_NULL = -1;

    // armor
    public static final int SLOT_HELMET = 103;
    public static final int SLOT_CHESTPLATE = 102;
    public static final int SLOT_LEGGNINGS = 101;
    public static final int SLOT_BOOTS = 100;

    private int slot;

    public ItemStackContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        if (this.hasSlot()) {
            inventory.setItem(this.getSlot(), this.getResult());
        } else {
            inventory.addItem(this.getResult());
        }
    }

    @Override
    public void revoke(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.getInventory().remove(this.getResult());
        }
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasSlot() {
        return this.slot != SLOT_NULL;
    }

    public void resetSlot() {
        this.setSlot(SLOT_NULL);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<ItemStackContent> {
        @Override
        public ItemStackContent parse(Element xml) throws DataConversionException {
            ItemStackContent content = new ItemStackContent(XMLItemStack.parse(xml));

            int slot = XMLParser.parseInt(xml.getAttributeValue("slot"), SLOT_NULL);
            if (slot != SLOT_NULL) {
                content.setSlot(slot);
            }

            return content;
        }
    }
}
