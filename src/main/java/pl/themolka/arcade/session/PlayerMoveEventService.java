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

package pl.themolka.arcade.session;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;

@ServiceId("PlayerMoveEvent")
public class PlayerMoveEventService extends Service {
    /**
     * Bukkit's {@link PlayerMoveEvent} is not what we need. We can simply
     * cancel the last player movement using the setCanceled(...) method in
     * {@link PlayerMoveEvent}.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Game game = this.getPlugin().getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        World fromWorld = event.getFrom().getWorld();
        World toWorld = event.getTo().getWorld();
        if (fromWorld == null || !fromWorld.equals(toWorld) || !fromWorld.equals(game.getWorld())) {
            // We are not interested in such events.
            return;
        }

        GamePlayer player = game.resolve(event.getPlayer());
        if (player == null) {
            return;
        }

        PlayerMoveEvent wrapper = new PlayerMoveEvent(this.getPlugin(),
                player,
                event);
        this.getPlugin().getEventBus().publish(wrapper);

        if (wrapper.isCanceled()) {
            Location to = event.getFrom().clone();
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);

            event.setTo(to);
        }
    }
}
