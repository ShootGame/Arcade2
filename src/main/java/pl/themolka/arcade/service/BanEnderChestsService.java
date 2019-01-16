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

package pl.themolka.arcade.service;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * We need to disable ender chests on the whole server due to the plugin logic
 * incompatibility. Our multi-world system doesn't work with the global ender
 * chests. In the future, we could add a fix which could override ender chests
 * to be per-world compatible.
 */
@ServiceId("BanEnderChests")
public class BanEnderChestsService extends Service {
    public static final String ENDER_CHEST_MESSAGE = ChatColor.RED +
            "You may not %s Ender Chests on this server.";

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result != null && result.getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(String.format(ENDER_CHEST_MESSAGE, "craft"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format(ENDER_CHEST_MESSAGE, "open"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format(ENDER_CHEST_MESSAGE, "place"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestUse(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(String.format(ENDER_CHEST_MESSAGE, "use"));
        }
    }
}
