package pl.themolka.arcade.leak.core;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class CoreLeakEvent extends CoreEvent implements Cancelable {
    private boolean cancel;

    public CoreLeakEvent(ArcadePlugin plugin, Core core) {
        super(plugin, core);
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
