package pl.themolka.arcade.item;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder implements Builder<ItemStack> {
    private int amount = 1;
    private final List<String> description = new ArrayList<>();
    private String displayName;
    private short durability;
    private final List<ItemEnchantment> enchantments = new ArrayList<>();
    private final List<ItemFlag> flags = new ArrayList<>();
    private Material type = Material.AIR;
    private boolean unbreakable;

    @Override
    public ItemStack build() {
        ItemStack stack = new ItemStack(this.type);
        stack.addUnsafeEnchantments(this.enchantmentsLegacy());
        stack.setAmount(this.amount);
        stack.setDurability(this.durability);
        stack.setItemMeta(this.buildMeta(stack.getItemMeta()));
        return stack;
    }

    private ItemMeta buildMeta(ItemMeta meta) {
        meta.addItemFlags(this.flags.toArray(new ItemFlag[this.flags.size()]));
        meta.setDisplayName(this.displayName);
        meta.setLore(this.description);
        meta.setUnbreakable(this.unbreakable);
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
        return new ArrayList<>(this.description);
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

    public ItemStackBuilder enchantment(ItemEnchantment enchantment) {
        this.enchantments.add(enchantment);
        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        return this.enchantment(new ItemEnchantment(enchantment, level));
    }

    public List<ItemEnchantment> enchantments() {
        return new ArrayList<>(this.enchantments);
    }

    public ItemStackBuilder enchantments(List<ItemEnchantment> enchantments) {
        this.enchantments.addAll(enchantments);
        return this;
    }

    public Map<Enchantment, Integer> enchantmentsLegacy() {
        return ItemEnchantment.toLegacy(this.enchantments);
    }

    /**
     * @deprecated {@link #enchantments(List)}
     */
    @Deprecated
    public ItemStackBuilder enchantmentsLegacy(Map<Enchantment, Integer> enchantments) {
        this.enchantments.addAll(ItemEnchantment.fromLegacy(enchantments));
        return this;
    }

    public ItemStackBuilder flag(ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public List<ItemFlag> flags() {
        return new ArrayList<>(this.flags);
    }

    public ItemStackBuilder flags(List<ItemFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    public ItemStackBuilder flags(ItemFlag... flags) {
        return this.flags(Arrays.asList(flags));
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
