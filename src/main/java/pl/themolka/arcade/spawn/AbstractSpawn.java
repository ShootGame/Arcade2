package pl.themolka.arcade.spawn;

public abstract class AbstractSpawn implements Spawn {
    private final String id;

    protected AbstractSpawn(Config<?> config) {
        this.id = config.id();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public interface Config<T extends AbstractSpawn> extends Spawn.Config<T> {
    }
}
