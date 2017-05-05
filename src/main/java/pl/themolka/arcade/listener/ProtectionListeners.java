package pl.themolka.arcade.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;

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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void antiLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ArcadePlayer arcade = this.plugin.getPlayer(player);
        if (arcade != null && arcade.getGamePlayer() != null && arcade.getGamePlayer().isParticipating()) {
            // call the fake event
            this.plugin.getServer().getPluginManager().callEvent(new PlayerDeathEvent(
                    player,
                    Arrays.asList(player.getInventory().getContents()),
                    player.getTotalExperience(),
                    null
            ));
        }
    }

    /**
     * People destroying a {@link Material#WORKBENCH} closes all viewers of this
     * craft window and drops their items on the ground. This is the major
     * reason why we need to disable this and open a fake workbench window
     * instead of the real one.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void safeWorkbenches(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasBlock()) {
            Block block = event.getClickedBlock();
            if (block.getType().equals(Material.WORKBENCH)) {
                event.setCancelled(true);
                event.getPlayer().openWorkbench(null, true);
            }
        }
    }
}
