package pl.themolka.arcade.capture.wool;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.util.Color;

public final class WoolUtils {
    public static final Material WOOL_MATERIAL = Material.WOOL;

    private WoolUtils() {
    }

    public static ChatColor chatColor(org.bukkit.material.Wool wool) {
        return color(wool).toChat();
    }

    public static Color color(org.bukkit.material.Wool wool) {
        return Color.ofDye(wool.getColor());
    }

    public static String coloredName(DyeColor color) {
        return Color.ofDye(color).toChat() + name(color);
    }

    public static String coloredName(org.bukkit.material.Wool wool) {
        return coloredName(wool.getColor());
    }

    public static boolean containsAny(Inventory inventory) {
        return count(inventory) > 0;
    }

    public static boolean containsAny(Inventory inventory, DyeColor color) {
        return count(inventory, color) > 0;
    }

    public static boolean containsAny(Inventory inventory, org.bukkit.material.Wool wool) {
        return containsAny(inventory, wool.getColor());
    }

    public static boolean containsAny(Inventory inventory, Wool wool) {
        return containsAny(inventory, wool.getColor());
    }

    public static int count(Inventory inventory) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (isWool(item)) {
                count = count + item.getAmount();
            }
        }

        return count;
    }

    public static int count(Inventory inventory, DyeColor color) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (isWool(item, color)) {
                count = count + item.getAmount();
            }
        }

        return count;
    }

    public static int count(Inventory inventory, org.bukkit.material.Wool wool) {
        return count(inventory, wool.getColor());
    }

    public static int count(Inventory inventory, Wool wool) {
        return count(inventory, wool.getColor());
    }

    public static org.bukkit.material.Wool fromItem(ItemStack item) {
        return item != null && isWool(item) ? ((org.bukkit.material.Wool) item.getData()) : null;
    }

    public static boolean isWool(Block block) {
        return block != null && isWool(block.getType());
    }

    public static boolean isWool(Block block, DyeColor color) {
        if (block == null) {
            return false;
        }

        MaterialData data = new MaterialData(block.getType(), block.getData());
        return color != null && isWool(data, color);
    }

    public static boolean isWool(ItemStack item) {
        return item != null && isWool(item.getType());
    }

    public static boolean isWool(ItemStack item, DyeColor color) {
        if (item != null && color != null) {
            org.bukkit.material.Wool wool = fromItem(item);
            if (wool != null) {
                return wool.getColor().equals(color);
            }
        }

        return false;
    }

    public static boolean isWool(Material material) {
        return material != null && material.equals(WOOL_MATERIAL);
    }

    public static boolean isWool(MaterialData data) {
        return data != null && isWool(data.getItemType());
    }

    public static boolean isWool(MaterialData data, DyeColor color) {
        return data != null && color != null && isWool(data) &&
                DyeColor.getByWoolData(data.getData()).equals(color);
    }

    public static String name(DyeColor color) {
        String[] name = StringUtils.split(color.name(), '_');
        for (int i = 0; i < name.length; i++) {
            name[i] = StringUtils.capitalize(name[i].toLowerCase());
        }

        return StringUtils.join(name, ' ');
    }

    public static String name(org.bukkit.material.Wool wool) {
        return name(wool.getColor());
    }
}
