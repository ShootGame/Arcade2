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

package pl.themolka.arcade.window;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;
import pl.themolka.arcade.session.ArcadePlayer;

@ServiceId("Window")
public class WindowService extends Service {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Window window = this.fetchWindow(event.getInventory());
        if (window == null) {
            return;
        }

        GamePlayer player = this.fetchPlayer(event.getWhoClicked());
        if (player == null) {
            return;
        }

        boolean click = window.click(player, event.getClick(), event.getSlot());
        if (!click) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        Window window = this.fetchWindow(event.getInventory());
        if (window == null) {
            return;
        }

        HumanEntity closer = event.getPlayer();
        GamePlayer player = this.fetchPlayer(closer);
        if (player == null) {
            return;
        }

        boolean close = window.close(player, false);
        if (!close) {
            // don't use the open(...) method in Window, because
            // it will handle the onOpen(...) event.
            closer.openInventory(window.getContainer());
        }
    }

    private GamePlayer fetchPlayer(HumanEntity human) {
        ArcadePlayer player = this.getPlugin().getPlayer(human);
        if (player != null) {
            return player.getGamePlayer();
        }

        return null;
    }

    private Window fetchWindow(Inventory view) {
        return this.getPlugin().getWindowRegistry().getWindow(view);
    }
}
