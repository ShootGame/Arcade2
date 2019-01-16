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

import net.minecraft.server.v1_13_R2.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_13_R2.attribute.CraftAttributeInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Attribute {
    private final AttributeInstance mojang;
    private final AttributeKey key;

    public Attribute(AttributeInstance mojang, AttributeKey key) {
        this.mojang = Objects.requireNonNull(mojang, "mojang cannot be null");
        this.key = Objects.requireNonNull(key, "key cannot be null");
    }

    public void addModifier(AttributeModifier modifier) {
        this.mojang.b(CraftAttributeInstance.convert(Objects.requireNonNull(modifier, "modifier cannot be null")));
    }

    public double computeValue() {
        return this.mojang.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute) {
            Attribute that = (Attribute) obj;
            return  this.mojang.equals(that.mojang) &&
                    this.key.equals(that.key);
        }

        return false;
    }

    public double getDefaultValue() {
        return this.mojang.getAttribute().getDefault();
    }

    public AttributeKey getKey() {
        return this.key;
    }

    public List<AttributeModifier> getModifiers() {
        List<AttributeModifier> modifiers = new ArrayList<>();
        for (net.minecraft.server.v1_13_R2.AttributeModifier modifier : this.mojang.c()) {
            modifiers.add(CraftAttributeInstance.convert(modifier));
        }

        return modifiers;
    }

    public double getValue() {
        return this.mojang.b();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mojang, this.key);
    }

    public int removeAllModifers() {
        int done = 0;
        for (net.minecraft.server.v1_13_R2.AttributeModifier modifier : new HashSet<>(this.mojang.c())) {
            this.mojang.c(modifier);
            done++;
        }

        return done;
    }

    public void removeModifier(AttributeModifier modifier) {
        this.mojang.c(CraftAttributeInstance.convert(Objects.requireNonNull(modifier, "modifier cannot be null")));
    }

    public void resetValue() {
        this.mojang.setValue(this.mojang.getAttribute().getDefault());
    }

    public void setValue(double baseValue) {
        this.mojang.setValue(baseValue);
    }
}
