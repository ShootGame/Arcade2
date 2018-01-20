package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;

/**
 * This is the parent class for all events related to the "PointCapture" class.
 */
public class PointCaptureEvent extends PointEvent {
    private final PointCapture capture;

    public PointCaptureEvent(ArcadePlugin plugin, PointCapture capture) {
        super(plugin, capture.getPoint());

        this.capture = capture;
    }

    public PointCapture getCapture() {
        return this.capture;
    }
}
