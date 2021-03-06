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

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.util.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A non-participating team observing the match and its players.
 */
public class Observers extends Team {
    public static final ChatColor OBSERVERS_CHAT_COLOR = ChatColor.AQUA;
    public static final DyeColor OBSERVERS_DYE_COLOR = Color.ofChat(OBSERVERS_CHAT_COLOR).toDye();
    public static final String OBSERVERS_KEY = "@";
    public static final String OBSERVERS_NAME = "Observers";
    public static final int OBSERVERS_SLOTS = Integer.MAX_VALUE;
    public static final String OBSERVERS_TEAM_ID = "_observers-team";

    protected Observers(Game game, Config config) {
        super(game, config);
    }

    @Override
    public boolean addGoal(Goal goal) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public boolean areGoalsCompleted() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public boolean canParticipate() {
        return false;
    }

    @Override
    public List<Goal> getGoals() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public int getMaxPlayers() {
        return this.getSlots();
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public Set<GamePlayer> getPlayers() {
        Set<GamePlayer> players = new HashSet<>();
        for (GamePlayer player : this.getGame().getPlayers().getOnlinePlayers()) {
            if (player.isParticipating()) {
                players.add(player);
            }
        }

        return players;
    }

    @Override
    public int getSlots() {
        return OBSERVERS_SLOTS;
    }

    @Override
    public boolean hasGoal(Goal goal) {
        return false;
    }

    @Override
    public boolean isFriendlyFire() {
        return true;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isObservers() {
        return true;
    }

    @Override
    public boolean isOverfilled() {
        return false;
    }

    @Override
    public boolean isParticipating() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean join(GamePlayer player, boolean message, boolean force) {
        boolean result = super.join(player, message, force);
        if (result) {
            player.getPlayer().clearInventory(true);
            player.setParticipating(false);
            player.getBukkit().setAllowFlight(true);

            this.getPlugin().getEventBus().publish(new ObserversJoinEvent(this.getPlugin(), player, this));
        }

        return result;
    }

    @Override
    public boolean leave(GamePlayer player) {
        boolean result = super.leave(player);
        if (result) {
            this.getPlugin().getEventBus().publish(new ObserversLeaveEvent(this.getPlugin(), player, this));
        }

        return result;
    }

    @Override
    public boolean removeGoal(Goal goal) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setFriendlyFire(boolean friendlyFire) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setMinPlayers(int minPlayers) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setParticipating(boolean participating) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setSlots(int slots) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    public interface Config extends Team.Config {
        default String id() { return OBSERVERS_TEAM_ID; }
        default Ref<ChatColor> chatColor() { return Ref.ofProvided(OBSERVERS_CHAT_COLOR); }
        default Ref<DyeColor> dyeColor() { return Ref.ofProvided(OBSERVERS_DYE_COLOR); }
        default Ref<Boolean> friendlyFire() { return Ref.ofProvided(true); }
        default Ref<Integer> minPlayers() { return Ref.ofProvided(0); }
        default Ref<Integer> maxPlayers() { return Ref.ofProvided(OBSERVERS_SLOTS); }
        default Ref<String> name() { return Ref.ofProvided(OBSERVERS_NAME); }
        default Ref<Integer> slots() { return Ref.ofProvided(OBSERVERS_SLOTS); }

        @Override
        default Team create(Game game, Library library) {
            return new Observers(game, this);
        }
    }
}
