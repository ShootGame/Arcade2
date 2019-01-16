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

import net.engio.mbassy.listener.Handler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;

public class MatchListeners implements Listener {
    private final MatchGame game;

    public MatchListeners(MatchGame game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void banBeds(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.HIGHEST)
    public void onBlockTransform(BlockTransformEvent event) {
        if (this.isMatchIdle()) {
            event.setCanceled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (this.isMatchIdle()) {
            event.setCancelled(true);
        }
    }

    private boolean isMatchIdle() {
        return !this.game.getMatch().isRunning();
    }
}
