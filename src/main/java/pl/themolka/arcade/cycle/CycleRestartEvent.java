package pl.themolka.arcade.cycle;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class CycleRestartEvent extends CycleEvent implements Cancelable {
    private boolean cancel;

    public CycleRestartEvent(ArcadePlugin plugin) {
        super(plugin, null);
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
