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

package pl.themolka.arcade.attribute;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.ItemAttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
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

import java.util.Collections;
import java.util.Set;

@Produces(ItemAttributeModifier.class)
public class ItemAttributeModifierParser extends NodeParser<ItemAttributeModifier>
                                         implements InstallableParser {
    private Parser<EquipmentSlot> slotParser;
    private Parser<AttributeModifier> modifierParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.slotParser = library.type(EquipmentSlot.class);
        this.modifierParser = library.type(AttributeModifier.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item attribute modifier");
    }

    @Override
    protected Result<ItemAttributeModifier> parseNode(Context context, Node node, String name, String value) throws ParserException {
        EquipmentSlot slot = this.slotParser.parse(context, node.property("slot", "equipment-slot", "equipmentslot")).orDefaultNull();
        AttributeModifier modifier = this.modifierParser.parseWithDefinition(context, node, name, value).orFail();

        return Result.fine(node, name, value, new ItemAttributeModifier(slot, modifier));
    }
}
