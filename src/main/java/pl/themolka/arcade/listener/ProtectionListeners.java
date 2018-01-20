package pl.themolka.arcade.listener;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.Arrays;

/**
 * Listeners related to anti-grief methods.
 */
public class ProtectionListeners implements Listener {
    private final ArcadePlugin plugin;

    public ProtectionListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

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
                    Arrays.asList(bukkit.getInventory().getContents()),
                    bukkit.getTotalExperience(),
                    null
            );

            // call the fake event
            this.plugin.getServer().getPluginManager().callEvent(death);
        }
    }

    /**
     * People destroying a {@link Material#WORKBENCH} closes all viewers of this
     * craft window and drops their items on the ground. This is the major
     * reason why we need to disable this and open a fake workbench window
     * instead of the real one.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void safeWorkbenches(PlayerInteractEvent event) {
        if (event.hasBlock() || !event.getPlayer().isSneaking()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();

                if (block.getType().equals(Material.WORKBENCH)) {
                    event.setCancelled(true);
                    event.getPlayer().openWorkbench(null, true);
                }
            }
        }
    }
}
