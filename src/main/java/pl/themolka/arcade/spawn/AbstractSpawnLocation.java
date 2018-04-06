package pl.themolka.arcade.spawn;

import org.bukkit.Location;

public abstract class AbstractSpawnLocation extends AbstractSpawn {
    protected AbstractSpawnLocation(Config<?> config) {
        super(config);
    }

    @Override
    public abstract Location getLocation();

    public interface Config<T extends AbstractSpawnLocation> extends AbstractSpawn.Config<T> {
    }
}
