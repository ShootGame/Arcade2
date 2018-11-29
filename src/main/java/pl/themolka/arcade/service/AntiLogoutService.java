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

import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ServiceId("AntiLogout")
public class AntiLogoutService extends Service {
    /**
     * Call a {@link PlayerDeathEvent} and handle this logout as a escape from
     * death by the enemy. If the player wasn't escaped the
     * {@link Player#getKiller()} returns null. This method is used to drop the
     * players items on the ground.
     */
    @Handler(priority = Priority.LAST)
    public void antiLogout(PlayerQuitEvent event) {
        GamePlayer player = event.getGamePlayer();
        Player bukkit = event.getBukkitPlayer();

        if (player != null && bukkit != null && player.isParticipating()) {
            PlayerDeathEvent death = new PlayerDeathEvent(
                    bukkit,
                    this.getDropsFor(bukkit),
                    bukkit.getTotalExperience(),
                    null
            );

            // call the fake event
            this.getServer().getPluginManager().callEvent(death);
        }
    }

    private List<ItemStack> getDropsFor(Player bukkit) {
        return new ArrayList<>(Arrays.asList(bukkit.getInventory().getContents()));
    }
}
