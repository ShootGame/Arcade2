package pl.themolka.arcade.capture;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class CapturableEvent extends GameEvent {
    private final Capturable capturable;

    public CapturableEvent(ArcadePlugin plugin, Capturable capturable) {
        super(plugin);

        this.capturable = capturable;
    }

    public Capturable getCapturable() {
        return this.capturable;
    }
}
