package pl.themolka.arcade.spawn;

import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

public abstract class AbstractSpawnVector extends AbstractSpawn {
    private final World world;

    protected AbstractSpawnVector(Game game, Config<?> config) {
        super(config);
        this.world = game.getWorld();
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

    public interface Config<T extends AbstractSpawnVector> extends AbstractSpawn.Config<T> {
    }
}
