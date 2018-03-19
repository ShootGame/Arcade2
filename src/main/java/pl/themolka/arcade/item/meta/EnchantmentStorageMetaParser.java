package pl.themolka.arcade.item.meta;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.item.ItemEnchantment;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(EnchantmentStorageMeta.class)
class EnchantmentStorageMetaParser extends ItemMetaParser.Nested<EnchantmentStorageMeta> {
    private Parser<ItemEnchantment> enchantmentParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.enchantmentParser = context.type(ItemEnchantment.class);
    }

    @Override
    public EnchantmentStorageMeta parse(Node root, ItemStack itemStack, EnchantmentStorageMeta itemMeta) throws ParserException {
        Node node = root.firstChild("enchanted-book");
        if (node != null) {
            for (Node enchantment : node.children("enchantment")) {
                ItemEnchantment value = this.enchantmentParser.parse(enchantment).orFail();
                itemMeta.addStoredEnchant(value.getType(), value.getLevel(), true);
            }
        }

        return itemMeta;
    }
}
