package pl.themolka.arcade.service;

import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ServiceId("AntiLogout")
public class AntiLogoutService extends Service {
    /**
     * Call a {@link PlayerDeathEvent} and handle this logout as a escape from
     * death by the enemy. If the player wasn't escaped the
     * {@link Player#getKiller()} returns null. This method is used to drop the
     * players items on the ground.
     */
    @Handler(priority = Priority.LAST)
    public void antiLogout(PlayerQuitEvent event) {
        GamePlayer player = event.getGamePlayer();
        Player bukkit = event.getBukkitPlayer();

        if (player != null && bukkit != null && player.isParticipating()) {
            PlayerDeathEvent death = new PlayerDeathEvent(
                    bukkit,
                    this.getDropsFor(bukkit),
                    bukkit.getTotalExperience(),
                    null
            );

            // call the fake event
            this.getServer().getPluginManager().callEvent(death);
        }
    }

    private List<ItemStack> getDropsFor(Player bukkit) {
        return new ArrayList<>(Arrays.asList(bukkit.getInventory().getContents()));
    }
}
