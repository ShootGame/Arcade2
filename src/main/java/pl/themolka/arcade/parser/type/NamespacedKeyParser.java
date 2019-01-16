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

package pl.themolka.arcade.parser.type;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(NamespacedKey.class)
public class NamespacedKeyParser extends ElementParser<NamespacedKey> {
    private static final String MINECRAFT_NS = "minecraft";

    @Override
    public Set<Object> expect() {
        return Collections.singleton("key with namespace (eg. '" + Material.GRASS_BLOCK.getKey() + "')");
    }

    @Override
    protected Result<NamespacedKey> parseElement(Context context, Element element, String name, String value) throws ParserException {
        String[] split = value.split(":");

        int length = split.length;
        if (length < 2) {
            throw this.fail(element, name, value, "No key (or namespace) present");
        } else if (length > 2) {
            throw this.fail(element, name, value, "Too many keys present");
        }

        String namespace = split[0];
        String key = split[1];

        if (!namespace.equals(MINECRAFT_NS)) {
            throw this.fail(element, name, value, namespace.equalsIgnoreCase(MINECRAFT_NS)
                    ? "Did you mean '" + this.format(key) + "'?"
                    : "Sorry, only '" + MINECRAFT_NS + "' namespace is currently supported");
        }

        try {
            return Result.fine(element, name, value, new NamespacedKey(namespace, key));
        } catch (IllegalArgumentException e) {
            throw this.fail(element, name, value, e.getMessage(), e);
        }
    }

    protected String format(String key) {
        return MINECRAFT_NS + ":" + key;
    }
}
