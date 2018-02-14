package pl.themolka.arcade.leak;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class LeakableLeakEvent extends LeakableEvent implements Cancelable {
    private boolean cancel;
    private Participator completer;

    public LeakableLeakEvent(ArcadePlugin plugin, Leakable leakable, Participator completer) {
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

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public void setCompleter(Participator completer) {
        this.completer = completer;
    }
}
