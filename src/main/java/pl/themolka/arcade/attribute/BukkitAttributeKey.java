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

import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.Attribute;
import pl.themolka.arcade.parser.EnumParser;

import java.util.Locale;
import java.util.Objects;

public class BukkitAttributeKey extends AttributeKey {
    private final Attribute bukkit;

    public BukkitAttributeKey(Attribute bukkit) {
        this.bukkit = Objects.requireNonNull(bukkit, "bukkit cannot be null");
    }

    @Override
    public String key() {
        String[] namespaceAndKey = this.bukkit.name().toLowerCase(Locale.US).split("_", 2);
        if (namespaceAndKey.length != 2) {
            throw new IllegalArgumentException();
        }

        StringBuilder result = new StringBuilder(namespaceAndKey[0]).append(".");

        String[] parts = namespaceAndKey[1].split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i != 0) {
                part = StringUtils.capitalize(part);
            }

            result.append(part);
        }

        return result.toString();
    }

    public Attribute getBukkit() {
        return this.bukkit;
    }

    //
    // Converting back to the Bukkit object
    //

    public static Attribute convert(BoundedModifier modifier) {
        return convert(Objects.requireNonNull(modifier, "modifier").getKey());
    }

    public static Attribute convert(AttributeKey key) {
        return convert(Objects.requireNonNull(key, "key cannot be null").key());
    }

    public static Attribute convert(String key) {
        return EnumParser.parse(Attribute.class, Objects.requireNonNull(key, "key cannot be null"));
    }

    public static Attribute convertOrFail(BoundedModifier modifier) {
        return convertOrFail(Objects.requireNonNull(modifier, "modifier cannot be null").getKey());
    }

    public static Attribute convertOrFail(AttributeKey key) {
        return convertOrFail(Objects.requireNonNull(key, "key cannot be null").key());
    }

    public static Attribute convertOrFail(String key) {
        Attribute bukkit = convert(key);
        if (bukkit != null) {
            return bukkit;
        }

        throw new IllegalArgumentException("Unknown attribute type");
    }
}
