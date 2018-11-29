package pl.themolka.arcade.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@ServiceId("SafeWorkbenches")
public class SafeWorkbenchesService extends Service {
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
