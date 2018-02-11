package pl.themolka.arcade.spawn;

import org.bukkit.Location;

public abstract class AbstractSpawnLocation implements Spawn {
    @Override
    public abstract Location getLocation();
}
