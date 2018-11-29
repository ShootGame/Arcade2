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

package pl.themolka.arcade.portal;

import org.bukkit.Sound;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.spawn.SpawnApply;

public class PlayerPortalEvent extends PortalEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer player;
    private SpawnApply destination;
    private Sound sound;

    public PlayerPortalEvent(ArcadePlugin plugin, Portal portal, GamePlayer player, SpawnApply destination, Sound sound) {
        super(plugin, portal);

        this.player = player;
        this.destination = destination;
        this.sound = sound;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public SpawnApply getDestination() {
        return this.destination;
    }

    public Sound getSound() {
        return this.sound;
    }

    public boolean isParticipating() {
        return this.getPlayer().isParticipating();
    }

    public void setDestination(SpawnApply destination) {
        this.destination = destination;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }
}
