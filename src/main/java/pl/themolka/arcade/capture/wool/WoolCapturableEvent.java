package pl.themolka.arcade.capture.wool;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class WoolCapturableEvent extends GameEvent {
    private final WoolCapturable wool;

    public WoolCapturableEvent(ArcadePlugin plugin, WoolCapturable wool) {
        super(plugin);

        this.wool = wool;
    }

    public WoolCapturable getWool() {
        return this.wool;
    }
}
