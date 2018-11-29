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

package pl.themolka.arcade.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;

public interface PlayerResolver {
    PlayerResolver NULL = new NullPlayerResolver();

    default GamePlayer resolve(ArcadePlayer player) {
        return player.getGamePlayer();
    }

    default GamePlayer resolve(CommandSender sender) {
        return sender instanceof Player ? this.resolve((Player) sender) : null;
    }

    default GamePlayer resolve(Entity entity) {
        return entity instanceof Player ? this.resolve((Player) entity) : null;
    }

    default GamePlayer resolve(GamePlayerSnapshot snapshot) {
        return this.resolve(snapshot.getUuid());
    }

    default GamePlayer resolve(Player bukkit) {
        return this.resolve(bukkit.getUniqueId());
    }

    GamePlayer resolve(String username);

    GamePlayer resolve(UUID uniqueId);

    interface Injector {
        void injectPlayerResolver(PlayerResolver playerResolver);
    }
}

class NullPlayerResolver implements PlayerResolver {
    @Override
    public GamePlayer resolve(String username) {
        return null;
    }

    @Override
    public GamePlayer resolve(UUID uniqueId) {
        return null;
    }
}
