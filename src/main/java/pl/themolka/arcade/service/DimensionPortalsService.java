package pl.themolka.arcade.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

/**
 * Other dimensions like Nether or The End are not supported. Players should
 * only play on the world loaded from their map manifest files.
 */
@ServiceId("DimensionPortals")
public class DimensionPortalsService extends Service {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortalCreate(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortalUse(EntityPortalEvent event) {
        event.setCancelled(true);
    }
}
