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
    public static final Material MATERIAL = Material.BANNER;
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
    public void setTypeId(int type) {
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
    public void setMaterial(MaterialData data) {
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
