package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;
import pl.themolka.arcade.xml.XMLParser;

public class ItemStackContent implements KitContent<ItemStack>, Removable {
    public static final int SLOT_NULL = -1;

    // armor
    public static final int SLOT_HELMET = 103;
    public static final int SLOT_CHESTPLATE = 102;
    public static final int SLOT_LEGGNINGS = 101;
    public static final int SLOT_BOOTS = 100;

    private final ItemStack result;
    private int slot;

    public ItemStackContent(ItemStack result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        PlayerInventory inventory = player.getBukkit().getInventory();
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

    @Override
    public ItemStack getResult() {
        return this.result;
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

    public static class Parser implements KitContentParser<ItemStackContent> {
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
