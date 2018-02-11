package pl.themolka.arcade.kit;

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
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        PlayerInventory inventory = player.getBukkit().getInventory();
        inventory.setHelmet(this.getHelmet());
        inventory.setChestplate(this.getChestplate());
        inventory.setLeggings(this.getLeggings());
        inventory.setBoots(this.getBoots());
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
            ItemStack helmet = XMLItemStack.parse(xml.getChild("helmet"));
            ItemStack chestplate = XMLItemStack.parse(xml.getChild("chestplate"));
            ItemStack leggings = XMLItemStack.parse(xml.getChild("leggings"));
            ItemStack boots = XMLItemStack.parse(xml.getChild("boots"));

            return helmet != null || chestplate != null || leggings != null || boots != null
                    ? new ArmorContent(helmet, chestplate, leggings, boots)
                    : null;
        }
    }
}
