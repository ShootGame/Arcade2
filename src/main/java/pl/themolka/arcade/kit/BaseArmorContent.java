package pl.themolka.arcade.kit;

import org.bukkit.inventory.ItemStack;

public abstract class BaseArmorContent extends BaseInventoryContent<ItemStack> {
    public BaseArmorContent(ItemStack result) {
        super(result);
    }
}
