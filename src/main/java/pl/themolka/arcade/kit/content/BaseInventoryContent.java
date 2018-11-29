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

import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.game.GamePlayer;

public abstract class BaseInventoryContent<T> implements KitContent<T> {
    private final T result;

    protected BaseInventoryContent(Config<?, T> config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        this.apply(player, player.getBukkit().getInventory());
    }

    @Override
    public T getResult() {
        return this.result;
    }

    public abstract void apply(GamePlayer player, PlayerInventory inventory);

    public interface Config<T extends BaseInventoryContent<?>, R> extends KitContent.Config<T, R> {
    }
}
