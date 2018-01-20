package pl.themolka.arcade.capture.flag;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagItem extends ItemStack {
    public static final Material MATERIAL = Material.BANNER;
    public static final int AMOUNT = 1;

    public FlagItem() {
        super(MATERIAL, AMOUNT);
    }

    @Override
    public Material getType() {
        return MATERIAL;
    }

    @Override
    public int getAmount() {
        return AMOUNT;
    }

    @Override
    public BannerMeta getItemMeta() {
        return (BannerMeta) super.getItemMeta();
    }

    @Override
    public void setType(Material type) {
    }

    @Override
    public void setAmount(int amount) {
    }

    @Override
    public boolean setItemMeta(ItemMeta meta) {
        if (meta instanceof BannerMeta) {
            return super.setItemMeta(meta);
        }

        throw new IllegalArgumentException("Flag's item meta must be an instance of the " +
                BannerMeta.class.getSimpleName() + " class.");
    }

    public BannerMeta transferMetaFrom(Banner banner) {
        return BannerUtils.toMeta(this.getItemMeta(), banner);
    }
}
