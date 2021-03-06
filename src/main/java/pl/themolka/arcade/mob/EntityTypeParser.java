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

package pl.themolka.arcade.mob;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Set;

@Produces(EntityType.class)
public class EntityTypeParser extends ElementParser<EntityType>
                              implements InstallableParser {
    private Parser<EntityType> entityTypeParser;
    private Parser<NamespacedKey> namespacedKeyParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.entityTypeParser = library.enumType(EntityType.class);
        this.namespacedKeyParser = library.type(NamespacedKey.class);
    }

    @Override
    public Set<Object> expect() {
        return this.entityTypeParser.expect();
    }

    @Override
    protected Result<EntityType> parseElement(Context context, Element element, String name, String value) throws ParserException {
        NamespacedKey namespacedKey = this.namespacedKeyParser.parseWithDefinition(context, element, name, value).orDefaultNull();
        String input = namespacedKey != null ? namespacedKey.getKey() : value;

        EntityType entityType = EntityType.fromName(input);
        if (entityType != null) {
            return Result.fine(element, name, input, entityType);
        }

        return this.entityTypeParser.parseWithDefinition(context, element, name, value);
    }
}
