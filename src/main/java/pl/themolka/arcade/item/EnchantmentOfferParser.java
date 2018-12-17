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

import org.bukkit.enchantments.EnchantmentOffer;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.PlayerLevel;

import java.util.Collections;
import java.util.Set;

@Produces(EnchantmentOffer.class)
public class EnchantmentOfferParser extends NodeParser<EnchantmentOffer>
                                    implements InstallableParser {
    private Parser<ItemEnchantment> enchantmentParser;
    private Parser<PlayerLevel> costParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.enchantmentParser = library.type(ItemEnchantment.class);
        this.costParser = library.type(PlayerLevel.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("enchantment offer");
    }

    @Override
    protected Result<EnchantmentOffer> parseNode(Context context, Node node, String name, String value) throws ParserException {
        ItemEnchantment enchantment = this.enchantmentParser.parseWithDefinition(context, node, name, value).orFail();

        int cost = this.costParser.parse(context, node.property("cost")).orFail().getLevel();
        if (cost <= 0) {
            throw this.fail(node, name, value, "Cost must be positive (greater than 0)");
        }

        EnchantmentOffer offer = new EnchantmentOffer(enchantment.getType(), enchantment.getLevel(), cost);
        return Result.fine(node, name, value, offer);
    }
}
