package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;

/**
 * This is the parent class for all events related to the "FlagSpawn" class.
 */
public class FlagSpawnEvent extends FlagEvent {
    private final FlagSpawn spawn;

    public FlagSpawnEvent(ArcadePlugin plugin, FlagSpawn spawn) {
        super(plugin, spawn.getFlag());

        this.spawn = spawn;
    }

    public FlagSpawn getSpawn() {
        return this.spawn;
    }
}
