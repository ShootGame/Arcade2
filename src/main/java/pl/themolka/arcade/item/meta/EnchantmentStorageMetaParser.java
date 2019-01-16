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

package pl.themolka.arcade.item.meta;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.item.ItemEnchantment;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(EnchantmentStorageMeta.class)
class EnchantmentStorageMetaParser extends ItemMetaParser.Nested<EnchantmentStorageMeta>
                                   implements InstallableParser {
    private Parser<ItemEnchantment> enchantmentParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.enchantmentParser = library.type(ItemEnchantment.class);
    }

    @Override
    public EnchantmentStorageMeta parse(Context context, Node root, ItemStack itemStack, EnchantmentStorageMeta itemMeta) throws ParserException {
        Node node = root.firstChild("enchanted-book", "enchantedbook");
        if (node != null) {
            for (Node enchantment : node.children("enchantment")) {
                this.enchantmentParser.parse(context, enchantment).orFail().apply(itemMeta);
            }
        }

        return itemMeta;
    }
}
