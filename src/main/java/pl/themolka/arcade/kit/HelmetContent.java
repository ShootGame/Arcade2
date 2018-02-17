package pl.themolka.arcade.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;

public class HelmetContent extends BaseArmorContent {
    public HelmetContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setHelmet(this.getResult());
    }

    public static class Parser implements KitContentParser<HelmetContent> {
        @Override
        public HelmetContent parse(Element xml) throws DataConversionException {
            return new HelmetContent(XMLItemStack.parse(xml));
        }
    }
}
