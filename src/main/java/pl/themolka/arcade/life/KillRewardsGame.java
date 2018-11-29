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

package pl.themolka.arcade.life;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KillRewardsGame extends GameModule {
    private final List<KillReward> rewards = new ArrayList<>();
    private final Multimap<UUID, KillReward> queuedRewards = ArrayListMultimap.create();

    protected KillRewardsGame(Game game, IGameConfig.Library library, Config config) {
        for (KillReward.Config reward : config.rewards().get()) {
            this.rewards.add(library.getOrDefine(game, reward));
        }
    }

    public boolean addReward(KillReward reward) {
        return this.rewards.add(reward);
    }

    public List<KillReward> getRewards() {
        return new ArrayList<>(this.rewards);
    }

    public List<KillReward> getRewardsFor(GamePlayer player) {
        List<KillReward> rewards = new ArrayList<>();
        for (KillReward reward : this.rewards) {
            if (reward.canReward(player)) {
                rewards.add(reward);
            }
        }

        return rewards;
    }

    public boolean removeReward(KillReward reward) {
        return this.rewards.remove(reward);
    }

    public void rewardPlayer(GamePlayer player, Iterable<KillReward> rewards) {
        for (KillReward reward : rewards) {
            reward.rewardPlayer(player);
        }

        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.updateInventory();
        }
    }

    //
    // Listening
    //

    @Handler(priority = Priority.LOWER)
    public void reward(PlayerDeathEvent event) {
        GamePlayer killer = event.getKiller();
        if (killer != null) {
            List<KillReward> rewards = this.getRewardsFor(killer);
            if (rewards.isEmpty()) {
                return;
            }

            if (killer.isDead()) {
                // Give rewards after the player respawned.
                this.queuedRewards.putAll(killer.getUuid(), rewards);
            } else {
                this.rewardPlayer(killer, rewards);
            }
        }
    }

    @Handler(priority = Priority.LOWER)
    public void rewardQueued(PlayerRespawnEvent event) {
        // We should have an event called after players are released...
        this.getServer().getScheduler().runTaskLater(this.getPlugin(), new Runnable() {
            @Override
            public void run() {
                GamePlayer player = event.getGamePlayer();
                if (player != null && player.isOnline()) {
                    rewardPlayer(player, queuedRewards.removeAll(player.getUuid()));
                }
            }
        }, Time.TICK.toTicks());
    }

    public interface Config extends IGameModuleConfig<KillRewardsGame> {
        Ref<List<KillReward.Config>> rewards();

        @Override
        default KillRewardsGame create(Game game, Library library) {
            return new KillRewardsGame(game, library, this);
        }
    }
}
