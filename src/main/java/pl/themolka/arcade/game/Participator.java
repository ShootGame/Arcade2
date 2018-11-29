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

import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.StringId;

import java.util.Collections;
import java.util.Set;

public interface Participator extends GoalHolder, Participartable, StringId {
    ToStringStyle TO_STRING_STYLE = ToStringStyle.NO_FIELD_NAMES_STYLE;

    boolean contains(Player bukkit);

    default boolean contains(ArcadePlayer player) {
        GamePlayer gamePlayer = player.getGamePlayer(); // should never be null
        return gamePlayer != null && this.contains(gamePlayer);
    }

    default boolean contains(GamePlayer player) {
        Player bukkit = player.getBukkit(); // null if the player if offline
        return bukkit != null && this.contains(bukkit);
    }

    Color getColor();

    default String getName() {
        return this.getId();
    }

    default Set<GamePlayer> getPlayers() {
        return Collections.emptySet();
    }

    default String getTitle() {
        return this.getColor().toChat() + this.getName() + ChatColor.RESET;
    }

    void sendGoalMessage(String message);

    interface Config<T extends Participator> extends IGameConfig<T> {
    }
}
