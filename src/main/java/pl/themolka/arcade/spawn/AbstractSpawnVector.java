package pl.themolka.arcade.spawn;

import org.bukkit.World;
import org.bukkit.util.Vector;

public abstract class AbstractSpawnVector implements Spawn {
    private final World world;

    public AbstractSpawnVector(World world) {
        this.world = world;
    }

    @Override
    public abstract Vector getVector();

    @Override
    public abstract float getYaw();

    @Override
    public abstract float getPitch();

    @Override
    public World getWorld() {
        return this.world;
    }
}
