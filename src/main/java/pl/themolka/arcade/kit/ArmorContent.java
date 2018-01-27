package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.XMLItemStack;

public class ArmorContent implements KitContent<ItemStack[]> {
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    public ArmorContent(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            PlayerInventory inventory = bukkit.getInventory();
            inventory.setHelmet(this.getHelmet());
            inventory.setChestplate(this.getChestplate());
            inventory.setLeggings(this.getLeggings());
            inventory.setBoots(this.getBoots());
        }
    }

    @Override
    public ItemStack[] getResult() {
        return new ItemStack[] {
                this.getHelmet(),
                this.getChestplate(),
                this.getLeggings(),
                this.getBoots()
        };
    }

    public ItemStack getHelmet() {
        return this.helmet;
    }

    public ItemStack getChestplate() {
        return this.chestplate;
    }

    public ItemStack getLeggings() {
        return this.leggings;
    }

    public ItemStack getBoots() {
        return this.boots;
    }

    public static class Parser implements KitContentParser<ArmorContent> {
        @Override
        public ArmorContent parse(Element xml) throws DataConversionException {
            ItemStack helmetItem = null;
            ItemStack chestplateItem = null;
            ItemStack leggingsItem = null;
            ItemStack bootsItem = null;

            Element helmet = xml.getChild("helmet");
            if (helmet != null) {
                helmetItem = XMLItemStack.parse(helmet);
            }

            Element chestplate = xml.getChild("chestplate");
            if (chestplate != null) {
                chestplateItem = XMLItemStack.parse(chestplate);
            }

            Element leggings = xml.getChild("leggings");
            if (leggings != null) {
                leggingsItem = XMLItemStack.parse(leggings);
            }

            Element boots = xml.getChild("boots");
            if (boots != null) {
                bootsItem = XMLItemStack.parse(boots);
            }

            if (helmet != null || chestplate != null || leggings != null || boots != null) {
                return new ArmorContent(helmetItem, chestplateItem, leggingsItem, bootsItem);
            } else {
                return null;
            }
        }
    }
}
