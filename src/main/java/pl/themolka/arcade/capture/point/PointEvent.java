package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.capture.CapturableEvent;

public class PointEvent extends CapturableEvent {
    private final Point point;

    public PointEvent(ArcadePlugin plugin, Point point) {
        super(plugin, point);

        this.point = point;
    }

    public Point getPoint() {
        return this.point;
    }
}
