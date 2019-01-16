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

package pl.themolka.arcade.objective.flag;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Map;

public class FlagItem extends ItemStack {
    public static final Material MATERIAL = Material.WHITE_BANNER;
    public static final int AMOUNT = 1;

    public FlagItem(Banner block) {
        super(MATERIAL, AMOUNT);

        super.setItemMeta(BannerUtils.toMeta(this.getItemMeta(), block));
    }

    public FlagItem(Banner block, Flag flag) {
        this(block, flag.getColoredName());
    }

    public FlagItem(Banner block, String name) {
        this(block);

        BannerMeta bannerMeta = this.getItemMeta();
        bannerMeta.setDisplayName(ChatColor.GOLD + name);
        super.setItemMeta(bannerMeta);
    }

    @Override
    public BannerMeta getItemMeta() {
        return (BannerMeta) super.getItemMeta();
    }

    //
    // Unsupported Methods
    //

    @Override
    public void setType(Material type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAmount(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setData(MaterialData data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDurability(short durability) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnchantments(Map<Enchantment, Integer> enchantments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEnchantment(Enchantment ench, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        throw new UnsupportedOperationException();
    }
}
