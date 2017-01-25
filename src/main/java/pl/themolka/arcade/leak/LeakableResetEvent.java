package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class LeakableResetEvent extends LeakableEvent implements Cancelable {
    private boolean cancel;

    public LeakableResetEvent(ArcadePlugin plugin, Leakable leakable) {
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
