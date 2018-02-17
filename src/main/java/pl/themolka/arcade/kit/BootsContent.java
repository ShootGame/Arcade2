package pl.themolka.arcade.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;

public class BootsContent extends BaseArmorContent {
    public BootsContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setBoots(this.getResult());
    }

    public static class Parser implements KitContentParser<BootsContent> {
        @Override
        public BootsContent parse(Element xml) throws DataConversionException {
            return new BootsContent(XMLItemStack.parse(xml));
        }
    }
}
