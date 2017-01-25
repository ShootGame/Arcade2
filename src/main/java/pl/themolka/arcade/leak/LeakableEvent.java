package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class LeakableEvent extends GameEvent {
    private final Leakable leakable;

    public LeakableEvent(ArcadePlugin plugin, Leakable leakable) {
        super(plugin);

        this.leakable = leakable;
    }

    public Leakable getLeakable() {
        return this.leakable;
    }
}
