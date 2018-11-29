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

import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.game.GamePlayer;

public class PlayerEvent extends Event {
    private final ArcadePlayer player;

    public PlayerEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin);

        this.player = player;
    }

    public Player getBukkitPlayer() {
        return this.getPlayer().getBukkit();
    }

    public GamePlayer getGamePlayer() {
        return this.getPlayer().getGamePlayer();
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }
}
