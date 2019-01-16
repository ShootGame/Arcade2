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

package pl.themolka.arcade.objective.wool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.team.PlayerLeaveTeamEvent;
import pl.themolka.arcade.util.FinitePercentage;

public class WoolPickupTracker implements Listener {
    private final Wool wool;

    private final Multimap<Participator, GamePlayer> pickups = HashMultimap.create();

    public WoolPickupTracker(Wool wool) {
        this.wool = wool;
    }

    public boolean pickup(ItemStack itemStack, GamePlayer picker) {
        if (this.wool.isCompleted() || this.isNotWool(itemStack)) {
            return false;
        }

        Participator participator = this.wool.getParticipatorResolver().resolve(picker);
        if (participator == null || !this.wool.isCompletableBy(participator)) {
            return false;
        }

        boolean firstParticipatorPickup = !this.pickups.containsKey(participator);
        boolean firstPickerPickup = !this.pickups.containsValue(picker);
        FinitePercentage oldProgress = this.wool.getProgress();

        WoolPickupEvent event = new WoolPickupEvent(this.wool, firstParticipatorPickup,firstPickerPickup, itemStack, participator, picker);
        this.wool.getPlugin().getEventBus().publish(event);

        if (event.isCanceled() || !firstPickerPickup) {
            return false;
        }

        this.wool.getContributions().addContributor(picker);
        participator.sendGoalMessage(this.createPickupMessage(picker));

        this.pickups.put(participator, picker);
        GoalProgressEvent.call(this.wool, picker, oldProgress);
        return true;
    }

    public void resetAllPickups() {
        this.pickups.clear();
    }

    public void resetPickupsFor(GamePlayer player) {
        Participator participator = this.wool.getParticipatorResolver().resolve(player);
        if (participator != null) {
            this.pickups.remove(participator, player);
        }
    }

    //
    // Listeners
    //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupBetweenInventories(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (this.isNotWool(item)) {
            return;
        }

        HumanEntity human = event.getWhoClicked();
        if (human instanceof Player) {
            GamePlayer picker = this.wool.getGame().resolve((Player) human);
            if (picker != null && picker.isParticipating()) {
                this.pickup(item, picker);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupFromGround(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (this.isNotWool(item)) {
            return;
        }

        GamePlayer picker = this.wool.getGame().resolve(event.getEntity());
        if (picker != null && picker.isParticipating()) {
            this.pickup(item, picker);
        }
    }

    @Handler(priority = Priority.LAST)
    public void resetPickups(PlayerLeaveTeamEvent event) {
        if (!event.isCanceled()) {
            this.resetPickupsFor(event.getGamePlayer());
        }
    }

    @Handler(priority = Priority.LAST)
    public void resetPickups(PlayerDeathEvent event) {
        this.resetPickupsFor(event.getVictim());
    }

    //
    // Misc
    //

    private String createPickupMessage(GamePlayer picker) {
        return ChatColor.GOLD + picker.getDisplayName() + ChatColor.YELLOW + " picked up " +
                this.wool.describeOwner() + this.wool.describeObjective() + ChatColor.YELLOW + ".";
    }

    private boolean isNotWool(ItemStack item) {
        return !WoolUtils.isWool(item, this.wool.getColor());
    }
}
