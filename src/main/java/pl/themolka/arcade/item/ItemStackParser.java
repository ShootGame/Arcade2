package pl.themolka.arcade.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.item.meta.ItemMetaParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.type.MaterialParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(ItemStack.class)
public class ItemStackParser extends NodeParser<ItemStack>
                             implements InstallableParser {
    private ItemMetaParser itemMetaParser;

    private Parser<MaterialData> typeParser;
    private Parser<Integer> amountParser;
    private Parser<String> displayNameParser;
    private Parser<String> descriptionParser;
    private Parser<Short> durabilityParser;
    private Parser<ItemEnchantment> enchantmentParser;
    private Parser<Material> canDestroyParser;
    private Parser<Material> canPlaceOnParser;
    private Parser<Boolean> unbreakableParser;
    private Parser<ItemFlag> flagParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.itemMetaParser = new ItemMetaParser();
        this.itemMetaParser.install(context);

        this.typeParser = context.type(MaterialData.class);
        this.amountParser = context.type(Integer.class);
        this.displayNameParser = context.type(String.class);
        this.descriptionParser = context.type(String.class);
        this.durabilityParser = context.type(Short.class);
        this.enchantmentParser = context.type(ItemEnchantment.class);
        this.canDestroyParser = context.type(Material.class);
        this.canPlaceOnParser = context.type(Material.class);
        this.unbreakableParser = context.type(Boolean.class);
        this.flagParser = context.enumType(ItemFlag.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item stack");
    }

    @Override
    protected ParserResult<ItemStack> parseTree(Node node, String name) throws ParserException {
        Property amountProperty = node.property("amount", "total");
        Property durabilityProperty = node.property("durability");

        MaterialData type = this.typeParser.parse(node.property(MaterialParser.MATERIAL_ELEMENT_NAMES)).orFail();
        int amount = this.amountParser.parse(amountProperty).orDefault(1);
        String displayName = this.displayNameParser.parse(node.firstChild("name", "display-name")).orDefaultNull();
        List<String> description = this.parseDescription(node);
        short durability = this.durabilityParser.parse(durabilityProperty).orDefault((short) 0);
        List<ItemEnchantment> enchantments = this.parseEnchantments(node);
        List<Material> canDestroy = this.parseDestroy(node);
        List<Material> canPlaceOn = this.parseCanPlaceOn(node);
        boolean unbreakable = this.unbreakableParser.parse(node.property("unbreakable", "permanent")).orDefault(false);
        List<ItemFlag> flags = this.parseFlags(node);

        if (amount <= 0) {
            throw this.fail(amountProperty, "Amount must be positive (greater than 0)");
        } else if (type.getData() != 0 && durability != 0) {
            // Notch made a huge mistake here... :(
            throw this.fail(durabilityProperty, "Sorry! Durability cannot be combined with material data!");
        }

        ItemStack itemStack = new ItemStack(type, amount);
        itemStack.setDurability(durability);
        for (ItemEnchantment enchantment : enchantments) {
            enchantment.apply(itemStack);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (displayName != null) {
            itemMeta.setDisplayName(displayName);
        }
        if (!description.isEmpty()) {
            itemMeta.setLore(description);
        }
        itemMeta.setCanDestroy(canDestroy);
        itemMeta.setCanPlaceOn(canPlaceOn);
        itemMeta.setUnbreakable(unbreakable);
        itemMeta.addItemFlags(flags.toArray(new ItemFlag[flags.size()]));

        itemStack.setItemMeta(this.itemMetaParser.parse(node, itemStack, itemMeta));
        return ParserResult.fine(node, name, itemStack);
    }

    //
    // Description
    //

    private List<String> parseDescription(Node root) throws ParserException {
        Node description = root.firstChild("description", "lore");
        return description != null ? this.parseDescription0(description)
                                   : this.parseDescription0(root);
    }

    private List<String> parseDescription0(Node node) throws ParserException {
        List<String> description = new ArrayList<>();
        for (Node line : node.children("line")) {
            description.add(this.descriptionParser.parse(line).orFail());
        }

        return description;
    }

    //
    // Enchantments
    //

    private List<ItemEnchantment> parseEnchantments(Node root) throws ParserException {
        Node enchantments = root.firstChild("enchantments");
        return enchantments != null ? this.parseEnchantments0(enchantments)
                                    : this.parseEnchantments0(root);
    }

    private List<ItemEnchantment> parseEnchantments0(Node node) throws ParserException {
        List<ItemEnchantment> enchantments = new ArrayList<>();
        for (Node enchantment : node.children("enchantment")) {
            enchantments.add(this.enchantmentParser.parse(enchantment).orFail());
        }

        return enchantments;
    }

    //
    // Flags
    //

    private List<ItemFlag> parseFlags(Node root) throws ParserException {
        Node flags = root.firstChild("flags", "item-flags", "itemflags");
        return flags != null ? this.parseFlags0(flags)
                             : this.parseFlags0(root);
    }

    private List<ItemFlag> parseFlags0(Node node) throws ParserException {
        List<ItemFlag> flags = new ArrayList<>();
        for (Node flag : node.children("flag")) {
            flags.add(this.flagParser.parse(flag).orFail());
        }

        return flags;
    }

    //
    // Can Destroy
    //

    private List<Material> parseDestroy(Node root) throws ParserException {
        Node group = root.firstChild("can-destroy");
        return group != null ? this.parseDestroy0(group, MaterialParser.MATERIAL_ELEMENT_NAMES)
                             : this.parseDestroy0(root, "destroy", "can-destroy");
    }

    private List<Material> parseDestroy0(Node node, String... names) throws ParserException {
        List<Material> materials = new ArrayList<>();
        for (Node material : node.children(names)) {
            materials.add(this.canDestroyParser.parse(material).orFail());
        }

        return materials;
    }

    //
    // Can Place On
    //

    private List<Material> parseCanPlaceOn(Node root) throws ParserException {
        Node group = root.firstChild("can-place-on");
        return group != null ? this.parseCanPlaceOn0(group, MaterialParser.MATERIAL_ELEMENT_NAMES)
                             : this.parseCanPlaceOn0(root, "place-on", "on", "can-place");
    }

    private List<Material> parseCanPlaceOn0(Node node, String... names) throws ParserException {
        List<Material> materials = new ArrayList<>();
        for (Node material : node.children(names)) {
            materials.add(this.canPlaceOnParser.parse(material).orFail());
        }

        return materials;
    }
}
