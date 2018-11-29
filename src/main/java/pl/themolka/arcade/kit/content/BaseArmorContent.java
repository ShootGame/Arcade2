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

package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.game.GamePlayer;

public abstract class BaseArmorContent extends BaseInventoryContent<ItemStack>
                                       implements RemovableKitContent<ItemStack> {
    protected BaseArmorContent(Config<?> config) {
        super(config);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        this.attach(player, this.getResult());
    }

    @Override
    public void attach(GamePlayer player, ItemStack value) {
        this.attach(player.getBukkit().getInventory(), value);
    }

    @Override
    public ItemStack defaultValue() {
        return Config.REMOVE;
    }

    public abstract void attach(PlayerInventory inventory, ItemStack value);

    public interface Config<T extends BaseArmorContent> extends BaseInventoryContent.Config<T, ItemStack>,
                                                                RemovableKitContent.Config<T, ItemStack> {
        ItemStack REMOVE = null;
    }
}
