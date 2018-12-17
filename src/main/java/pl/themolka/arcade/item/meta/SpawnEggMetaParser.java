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

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(SpawnEggMeta.class)
class SpawnEggMetaParser extends ItemMetaParser.Nested<SpawnEggMeta>
                         implements InstallableParser {
    private Parser<EntityType> spawnedTypeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.spawnedTypeParser = library.type(EntityType.class);
    }

    public SpawnEggMeta parse(Context context, Node root, ItemStack itemStack, SpawnEggMeta itemMeta) throws ParserException {
        Node node = root.firstChild("spawner-egg");
        if (node != null) {
            Property spawnedType = node.property("spawned-type", "entity-type", "mob-type");
            if (spawnedType != null) {
                itemMeta.setSpawnedType(this.spawnedTypeParser.parse(context, spawnedType).orFail());
            }
        }

        return itemMeta;
    }
}
