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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.themolka.arcade.util.Applicable;

import java.util.Objects;

/**
 * Wrapping an {@link Enchantment} and its level into a single class.
 */
public class ItemEnchantment implements Applicable<ItemStack> {
    private final Enchantment type;
    private final int level;

    public ItemEnchantment(Enchantment type, int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("level must be greater than 0");
        }

        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.level = level;
    }

    @Override
    public void apply(ItemStack itemStack) {
        if (itemStack != null) {
            itemStack.addUnsafeEnchantment(this.type, this.level);
        }
    }

    public void apply(ItemMeta itemMeta) {
        if (itemMeta != null) {
            itemMeta.addEnchant(this.type, this.level, true);
        }
    }

    public Enchantment getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }
}
