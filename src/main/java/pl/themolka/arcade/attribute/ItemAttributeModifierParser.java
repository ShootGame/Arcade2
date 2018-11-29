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
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
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
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.slotParser = context.type(EquipmentSlot.class);
        this.modifierParser = context.type(AttributeModifier.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item attribute modifier");
    }

    @Override
    protected Result<ItemAttributeModifier> parseNode(Node node, String name, String value) throws ParserException {
        EquipmentSlot slot = this.slotParser.parse(node.property("slot", "equipment-slot", "equipmentslot")).orDefaultNull();
        AttributeModifier modifier = this.modifierParser.parseWithDefinition(node, name, value).orFail();

        return Result.fine(node, name, value, new ItemAttributeModifier(slot, modifier));
    }
}
