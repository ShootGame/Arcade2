package pl.themolka.arcade.capture.wool;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.CapturableEvent;

public class WoolCapturableEvent extends CapturableEvent {
    private final WoolCapturable wool;

    public WoolCapturableEvent(ArcadePlugin plugin, WoolCapturable wool) {
        super(plugin, wool);

        this.wool = wool;
    }

    public WoolCapturable getWool() {
        return this.wool;
    }
}
