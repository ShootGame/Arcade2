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

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(KnowledgeBookMeta.class)
public class KnowledgeBookMetaParser extends ItemMetaParser.Nested<KnowledgeBookMeta>
                                     implements InstallableParser {
    private Parser<NamespacedKey> recipeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.recipeParser = library.type(NamespacedKey.class);
    }

    @Override
    public KnowledgeBookMeta parse(Context context, Node root, ItemStack itemStack, KnowledgeBookMeta itemMeta) throws ParserException {
        Node node = root.firstChild("knowledge-book", "knowledgebook", "recipe-book", "recipebook", "recipes");
        if (node != null) {
            for (Node recipe : node.children("recipe", "discover")) {
                itemMeta.addRecipe(this.recipeParser.parse(context, recipe).orFail());
            }
        }

        return itemMeta;
    }
}
