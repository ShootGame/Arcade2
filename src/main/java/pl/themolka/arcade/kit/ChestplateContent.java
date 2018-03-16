package pl.themolka.arcade.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;

public class ChestplateContent extends BaseArmorContent {
    public ChestplateContent(ItemStack result) {
        super(result);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        inventory.setChestplate(this.getResult());
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<ChestplateContent> {
        @Override
        public ChestplateContent parse(Element xml) throws DataConversionException {
            return new ChestplateContent(XMLItemStack.parse(xml));
        }
    }
}
