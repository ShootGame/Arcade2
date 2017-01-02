package pl.themolka.arcade.item;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder implements Builder<ItemStack> {
    private int amount;
    private final List<String> description = new ArrayList<>();
    private String displayName;
    private short durability;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Material type;
    private boolean unbreakable;

    @Override
    public ItemStack build() {
        ItemStack stack = new ItemStack(this.type());
        stack.addUnsafeEnchantments(this.enchantments());
        stack.setAmount(this.amount());
        stack.setDurability(this.durability());
        stack.setItemMeta(this.buildMeta(stack.getItemMeta()));

        return stack;
    }

    private ItemMeta buildMeta(ItemMeta meta) {
        meta.setDisplayName(this.displayName());
        meta.setLore(this.description());
        meta.setUnbreakable(this.unbreakable());

        return meta;
    }

    public int amount() {
        return this.amount;
    }

    public ItemStackBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public List<String> description() {
        return this.description;
    }

    public ItemStackBuilder description(String description) {
        this.description.add(description);
        return this;
    }

    public ItemStackBuilder description(List<String> description) {
        this.description.addAll(description);
        return this;
    }

    public ItemStackBuilder description(String... description) {
        return this.description(Arrays.asList(description));
    }

    public String displayName() {
        return this.displayName;
    }

    public ItemStackBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public short durability() {
        return this.durability;
    }

    public ItemStackBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public Map<Enchantment, Integer> enchantments() {
        return this.enchantments;
    }

    public ItemStackBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    public Material type() {
        return this.type;
    }

    public ItemStackBuilder type(Material type) {
        this.type = type;
        return this;
    }

    public boolean unbreakable() {
        return this.unbreakable;
    }

    public ItemStackBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }
}
