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

import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.ItemFlag;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Produces(ItemFlag.class)
public class ItemFlagParser extends ElementParser<ItemFlag>
                            implements InstallableParser {
    private static final Map<String, ItemFlag> BINDINGS = ImmutableMap.<String, ItemFlag>builder()
            .put("hide destroy keys", ItemFlag.HIDE_DESTROYS)
            .put("hide destroyable keys", ItemFlag.HIDE_DESTROYS)
            .put("hide enchantments", ItemFlag.HIDE_ENCHANTS)
            .put("hide place keys", ItemFlag.HIDE_PLACED_ON)
            .put("hide place on keys", ItemFlag.HIDE_PLACED_ON)
            .put("hide placeable keys", ItemFlag.HIDE_PLACED_ON)
            .put("hide placeable on keys", ItemFlag.HIDE_PLACED_ON)
            .put("hide effects", ItemFlag.HIDE_POTION_EFFECTS)
            .build();

    private Parser<ItemFlag> flagParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.flagParser = library.enumType(ItemFlag.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item flag");
    }

    @Override
    protected Result<ItemFlag> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String input = EnumParser.toPrettyValue(value);

        ItemFlag maybeBinding = BINDINGS.get(input);
        if (maybeBinding != null) {
            return Result.fine(element, name, value, maybeBinding);
        }

        return this.flagParser.parseWithDefinition(context, element, name, value);
    }
}
