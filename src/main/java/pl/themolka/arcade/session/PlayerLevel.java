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

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

import java.util.Objects;

public class PlayerLevel implements PlayerApplicable {
    public static final PlayerLevel ZERO = new PlayerLevel(0);

    private final int level;

    public PlayerLevel(int level) {
        Validate.isTrue(level >= 0, "level cannot be negative");
        this.level = level;
    }

    public PlayerLevel(Player bukkit) {
        this(Objects.requireNonNull(bukkit, "bukkit cannot be null").getLevel());
    }

    @Override
    public void apply(GamePlayer player) {
        player.setLevel(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerLevel && Objects.equals(this.level, ((PlayerLevel) obj).level);
    }

    public void applyIncremental(GamePlayer player) {
        PlayerLevel old = player.getLevel();
        player.setLevel(old.increment(this));
    }

    public PlayerLevel decrement(PlayerLevel level) {
        return new PlayerLevel(this.level - level.level);
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.level);
    }

    public PlayerLevel increment(PlayerLevel level) {
        return new PlayerLevel(this.level + level.level);
    }

    public boolean isZero() {
        return ZERO.equals(this);
    }

    public PlayerLevel negate() {
        return new PlayerLevel(-this.level);
    }

    public static PlayerLevel getDefaultLevel() {
        return ZERO;
    }
}
