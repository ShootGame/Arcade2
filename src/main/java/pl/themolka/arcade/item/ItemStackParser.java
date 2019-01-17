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

import com.destroystokyo.paper.Namespaced;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.themolka.arcade.attribute.BoundedModifier;
import pl.themolka.arcade.attribute.BukkitAttributeKey;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.item.meta.ItemMetaParser;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.parser.type.MaterialParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Produces(ItemStack.class)
public class ItemStackParser extends NodeParser<ItemStack>
                             implements InstallableParser {
    private ItemMetaParser itemMetaParser;

    private Parser<Material> typeParser;
    private Parser<Integer> amountParser;
    private Parser<String> displayNameParser;
    private Parser<String> descriptionParser;
    private Parser<Short> durabilityParser;
    private Parser<ItemEnchantment> enchantmentParser;
    private Parser<NamespacedKey> destroyableKeysParser;
    private Parser<NamespacedKey> placeableKeysParser;
    private Parser<BoundedModifier> modifierParser;
    private Parser<Boolean> unbreakableParser;
    private Parser<ItemFlag> flagParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.itemMetaParser = new ItemMetaParser();
        this.itemMetaParser.install(library);

        this.typeParser = library.type(Material.class);
        this.amountParser = library.type(Integer.class);
        this.displayNameParser = library.type(String.class);
        this.descriptionParser = library.type(String.class);
        this.durabilityParser = library.type(Short.class);
        this.enchantmentParser = library.type(ItemEnchantment.class);
        this.destroyableKeysParser = library.type(NamespacedKey.class);
        this.placeableKeysParser = library.type(NamespacedKey.class);
        this.modifierParser = library.type(BoundedModifier.class);
        this.unbreakableParser = library.type(Boolean.class);
        this.flagParser = library.type(ItemFlag.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item stack");
    }

    @Override
    protected Result<ItemStack> parseTree(Context context, Node node, String name) throws ParserException {
        Property amountProperty = node.property("amount", "total");
        Property durabilityProperty = node.property("durability");

        Material type = this.typeParser.parse(context, node.property(MaterialParser.MATERIAL_ELEMENT_NAMES)).orFail();
        int amount = this.amountParser.parse(context, amountProperty).orDefault(1);
        String displayName = this.displayNameParser.parse(context, node.firstChild("name", "display-name")).orDefaultNull();
        List<String> description = this.parseDescription(context, node);
        short durability = this.durabilityParser.parse(context, durabilityProperty).orDefault((short) 0);
        List<ItemEnchantment> enchantments = this.parseEnchantments(context, node);
        Set<Namespaced> destroyableKeys = this.parseDestroyableKeys(context, node);
        Set<Namespaced> placeableKeys = this.parsePlaceableKeys(context, node);
        boolean unbreakable = this.unbreakableParser.parse(context, node.property("unbreakable", "permanent")).orDefault(false);
        List<ItemFlag> flags = this.parseFlags(context, node);

        if (amount <= 0) {
            throw this.fail(amountProperty, "Amount must be positive (greater than 0)");
        }

        ItemStack itemStack = new ItemStack(type);
        itemStack.setAmount(amount);
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

        if (!destroyableKeys.isEmpty()) {
            itemMeta.setDestroyableKeys(destroyableKeys);
        }
        if (!placeableKeys.isEmpty()) {
            itemMeta.setPlaceableKeys(placeableKeys);
        }

        for (Node modifierNode : node.children("modifier", "attribute-modifier", "attributemodifier", "attribute")) {
            BoundedModifier modifier = this.modifierParser.parse(context, modifierNode).orFail();
            itemMeta.addAttributeModifier(BukkitAttributeKey.convertOrFail(modifier), modifier.getModifier());
        }

        itemMeta.setUnbreakable(unbreakable);
        itemMeta.addItemFlags(flags.toArray(new ItemFlag[flags.size()]));

        itemStack.setItemMeta(this.itemMetaParser.parse(context, node, itemStack, itemMeta));
        return Result.fine(node, name, itemStack);
    }

    //
    // Description
    //

    private List<String> parseDescription(Context context, Node root) throws ParserException {
        Node description = root.firstChild("description", "lore");
        return description != null ? this.parseDescription0(context, description)
                                   : this.parseDescription0(context, root);
    }

    private List<String> parseDescription0(Context context, Node node) throws ParserException {
        List<String> description = new ArrayList<>();
        for (Node line : node.children("line")) {
            description.add(this.descriptionParser.parse(context, line).orFail());
        }

        return description;
    }

    //
    // Enchantments
    //

    private List<ItemEnchantment> parseEnchantments(Context context, Node root) throws ParserException {
        Node enchantments = root.firstChild("enchantments");
        return enchantments != null ? this.parseEnchantments0(context, enchantments)
                                    : this.parseEnchantments0(context, root);
    }

    private List<ItemEnchantment> parseEnchantments0(Context context, Node node) throws ParserException {
        List<ItemEnchantment> enchantments = new ArrayList<>();
        for (Node enchantment : node.children("enchantment")) {
            enchantments.add(this.enchantmentParser.parse(context, enchantment).orFail());
        }

        return enchantments;
    }

    //
    // Flags
    //

    private List<ItemFlag> parseFlags(Context context, Node root) throws ParserException {
        Node flags = root.firstChild("flags", "item-flags", "itemflags");
        return flags != null ? this.parseFlags0(context, flags)
                             : this.parseFlags0(context, root);
    }

    private List<ItemFlag> parseFlags0(Context context, Node node) throws ParserException {
        List<ItemFlag> flags = new ArrayList<>();
        for (Node flag : node.children("flag")) {
            flags.add(this.flagParser.parse(context, flag).orFail());
        }

        return flags;
    }

    //
    // Can Destroy
    //

    private Set<Namespaced> parseDestroyableKeys(Context context, Node root) throws ParserException {
        Node group = root.firstChild("destroyable", "destroyable-keys", "destroyablekeys", "can-destroy", "candestroy");
        return group != null ? this.parseDestroyableKeys0(context, group, MaterialParser.MATERIAL_ELEMENT_NAMES)
                             : this.parseDestroyableKeys0(context, root, "destroy", "can-destroy", "candestroy");
    }

    private Set<Namespaced> parseDestroyableKeys0(Context context, Node node, String... names) throws ParserException {
        Set<Namespaced> keys = new HashSet<>();
        for (Node key : node.children(names)) {
            keys.add(this.destroyableKeysParser.parse(context, key).orFail());
        }

        return keys;
    }

    //
    // Can Place On
    //

    private Set<Namespaced> parsePlaceableKeys(Context context, Node root) throws ParserException {
        Node group = root.firstChild("placable", "placable-keys", "placablekeys", "can-place-on", "canplaceon");
        return group != null ? this.parsePlaceableKeys0(context, group, MaterialParser.MATERIAL_ELEMENT_NAMES)
                             : this.parsePlaceableKeys0(context, root, "place-on", "placeon", "can-place", "canplace");
    }

    private Set<Namespaced> parsePlaceableKeys0(Context context, Node node, String... names) throws ParserException {
        Set<Namespaced> keys = new HashSet<>();
        for (Node key : node.children(names)) {
            keys.add(this.placeableKeysParser.parse(context, key).orFail());
        }

        return keys;
    }
}
