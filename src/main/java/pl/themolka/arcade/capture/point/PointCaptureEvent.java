package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;

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
