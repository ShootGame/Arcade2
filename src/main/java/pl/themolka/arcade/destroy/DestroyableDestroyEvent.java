package pl.themolka.arcade.destroy;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class DestroyableDestroyEvent extends DestroyableEvent implements Cancelable {
    private boolean cancel;
    private Participator completer;

    public DestroyableDestroyEvent(ArcadePlugin plugin, Destroyable destroyable, Participator completer) {
        super(plugin, destroyable);

        this.completer = completer;
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
