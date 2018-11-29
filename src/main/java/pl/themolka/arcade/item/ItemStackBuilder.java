/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.item;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
        stack.addUnsafeEnchantments(this.enchantmentsBukkit());
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

    Map<Enchantment, Integer> enchantmentsBukkit() {
        Map<Enchantment, Integer> bukkit = new LinkedHashMap<>(this.enchantments.size());
        for (ItemEnchantment enchantment : this.enchantments) {
            bukkit.put(enchantment.getType(), enchantment.getLevel());
        }

        return bukkit;
    }
}
