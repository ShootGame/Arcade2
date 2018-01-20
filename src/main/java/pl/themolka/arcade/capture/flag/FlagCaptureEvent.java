package pl.themolka.arcade.capture.flag;

import pl.themolka.arcade.ArcadePlugin;

/**
 * This is the parent class for all events related to the "FlagCapture" class.
 */
public class FlagCaptureEvent extends FlagEvent {
    private final FlagCapture capture;

    public FlagCaptureEvent(ArcadePlugin plugin, FlagCapture capture) {
        super(plugin, capture.getFlag());

        this.capture = capture;
    }

    public FlagCapture getCapture() {
        return this.capture;
    }
}
