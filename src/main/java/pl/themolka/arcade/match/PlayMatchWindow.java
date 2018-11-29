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

package pl.themolka.arcade.match;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.window.Window;

public class PlayMatchWindow extends Window {
    private final Match match;

    public PlayMatchWindow(Match match, int rows, String name) {
        super(match.getGame().getPlugin(), rows, name);

        this.match = match;
    }

    @Override
    public boolean onClick(GamePlayer player, ClickType click, int slot, ItemStack item) {
        if (slot == this.getCloseItemSlot()) {
            this.close(player);
            return false;
        }

        return super.onClick(player, click, slot, item);
    }

    @Override
    public void onCreate() {
        this.getContainer().setItem(this.getCloseItemSlot(), CLOSE_ITEM);

        super.onCreate();
    }

    @Override
    public boolean onOpen(GamePlayer player) {
        if (this.getMatch().isCycling()) {
            player.sendError(MatchGame.JOIN_ON_CYCLE_MESSAGE);
            return false;
        }

        return super.onOpen(player);
    }

    public int getCloseItemSlot() {
        return this.getContainer().getSize() - 1;
    }

    public Match getMatch() {
        return this.match;
    }
}
