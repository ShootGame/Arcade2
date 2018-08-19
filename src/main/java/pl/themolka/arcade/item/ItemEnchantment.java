package pl.themolka.arcade.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.themolka.arcade.util.Applicable;

import java.util.Objects;

/**
 * Wrapping an {@link Enchantment} and its level into a single class.
 */
public class ItemEnchantment implements Applicable<ItemStack> {
    private final Enchantment type;
    private final int level;

    public ItemEnchantment(Enchantment type, int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("level must be greater than 0");
        }

        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.level = level;
    }

    @Override
    public void apply(ItemStack itemStack) {
        if (itemStack != null) {
            itemStack.addUnsafeEnchantment(this.type, this.level);
        }
    }

    public void apply(ItemMeta itemMeta) {
        if (itemMeta != null) {
            itemMeta.addEnchant(this.type, this.level, true);
        }
    }

    public Enchantment getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }
}
