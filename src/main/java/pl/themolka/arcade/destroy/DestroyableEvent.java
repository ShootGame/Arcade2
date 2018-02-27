package pl.themolka.arcade.destroy;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class DestroyableEvent extends GameEvent {
    private final Destroyable destroyable;

    public DestroyableEvent(ArcadePlugin plugin, Destroyable destroyable) {
        super(plugin);

        this.destroyable = destroyable;
    }

    public Destroyable getDestroyable() {
        return this.destroyable;
    }
}
