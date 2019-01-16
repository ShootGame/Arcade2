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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@ServiceId("SafeWorkbenches")
public class SafeWorkbenchesService extends Service {
    /**
     * People destroying a {@link Material#CRAFTING_TABLE} closes all viewers of this
     * craft window and drops their items on the ground. This is the major
     * reason why we need to disable this and open a fake workbench window
     * instead of the real one.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void safeWorkbenches(PlayerInteractEvent event) {
        Player opener = event.getPlayer();
        if (!opener.isSneaking() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();

            if (block != null && block.getType().equals(Material.CRAFTING_TABLE)) {
                event.setCancelled(true);
                opener.openWorkbench(null, true);
            }
        }
    }
}
