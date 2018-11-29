package pl.themolka.arcade.service;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;

@ServiceId("WorldInitEvent")
public class WorldInitEventService extends Service {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setSpawnFlags(false, false);
    }
}
