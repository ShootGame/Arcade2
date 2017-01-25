package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class LeakableLeakEvent extends LeakableEvent implements Cancelable {
    private boolean cancel;

    public LeakableLeakEvent(ArcadePlugin plugin, Leakable leakable) {
        super(plugin, leakable);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
