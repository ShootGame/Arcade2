package pl.themolka.arcade.item;

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnchantmentUtils {
    private EnchantmentUtils() {
    }

    public static List<ItemEnchantment> fromLegacy(Map<Enchantment, Integer> legacy) {
        List<ItemEnchantment> enchantments = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : legacy.entrySet()) {
            enchantments.add(new ItemEnchantment(entry.getKey(), entry.getValue()));
        }

        return enchantments;
    }

    public static Map<Enchantment, Integer> toLegacy(List<ItemEnchantment> enchantments) {
        Map<Enchantment, Integer> legacy = new LinkedHashMap<>();
        for (ItemEnchantment enchantment : enchantments) {
            legacy.put(enchantment.getType(), enchantment.getLevel());
        }

        return legacy;
    }
}
