package pl.themolka.arcade.leak.core;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.leak.LeakableEvent;

public class CoreEvent extends LeakableEvent {
    private final Core core;

    public CoreEvent(ArcadePlugin plugin, Core core) {
        super(plugin, core);

        this.core = core;
    }

    public Core getCore() {
        return this.core;
    }
}
