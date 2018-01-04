package pl.themolka.arcade.leak.core;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class CoreLeakableEvent extends GameEvent {
    private final CoreLeakable core;

    public CoreLeakableEvent(ArcadePlugin plugin, CoreLeakable core) {
        super(plugin);

        this.core = core;
    }

    public CoreLeakable getCore() {
        return this.core;
    }
}
