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

import org.bukkit.enchantments.Enchantment;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(ItemEnchantment.class)
public class ItemEnchantmentParser extends NodeParser<ItemEnchantment>
                                   implements InstallableParser {
    private Parser<Enchantment> typeParser;
    private Parser<Integer> levelParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.typeParser = library.type(Enchantment.class);
        this.levelParser = library.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item (leveled) enchantment");
    }

    @Override
    protected Result<ItemEnchantment> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        Enchantment type = this.typeParser.parseWithDefinition(context, node, name, value).orFail();

        Property levelProperty = node.property("level", "lvl");
        int level = this.levelParser.parse(context, levelProperty).orDefault(1);
        if (level <= 0) {
            throw this.fail(levelProperty, "Level must be positive (greater than 0)");
        }

        return Result.fine(node, name, value, new ItemEnchantment(type, level));
    }
}
