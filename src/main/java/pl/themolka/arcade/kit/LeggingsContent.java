package pl.themolka.arcade.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;

public class LeggingsContent extends BaseArmorContent {
    public LeggingsContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setLeggings(this.getResult());
    }

    public static class Parser implements KitContentParser<LeggingsContent> {
        @Override
        public LeggingsContent parse(Element xml) throws DataConversionException {
            return new LeggingsContent(XMLItemStack.parse(xml));
        }
    }
}
