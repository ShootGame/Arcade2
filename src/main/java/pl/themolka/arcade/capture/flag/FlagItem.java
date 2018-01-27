package pl.themolka.arcade.capture.flag;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagItem extends ItemStack {
    public static final Material MATERIAL = Material.BANNER;
    public static final int AMOUNT = 1;

    private final Flag flag;

    public FlagItem(Flag flag) {
        super(MATERIAL, AMOUNT);

        this.flag = flag;
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

    public Flag getFlag() {
        return this.flag;
    }

    public BannerMeta transferMetaFrom(Banner banner) {
        BannerMeta meta = this.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + this.flag.getColoredName());
        return BannerUtils.toMeta(meta, banner);
    }
}
