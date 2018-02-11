package pl.themolka.arcade.life;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KillRewardGame extends GameModule {
    private final List<KillReward> rewards = new ArrayList<>();
    private final Multimap<UUID, KillReward> queuedRewards = ArrayListMultimap.create();

    @Override
    public void onEnable() {
        FiltersGame filters = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        KitsGame kits = (KitsGame) this.getGame().getModule(KitsModule.class);

        for (Element xml : this.getSettings().getChildren("reward")) {
            Filter filter = Filters.undefined();
            if (filters != null) {
                filter = filters.filterOrDefault(xml.getAttributeValue("filter"), filter);
            }

            Kit kit = kits.getKit(xml.getValue());
            if (kit == null) {
                continue;
            }

            this.rewards.add(new KillReward(filter, kit));
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
            reward.apply(player);
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
}
