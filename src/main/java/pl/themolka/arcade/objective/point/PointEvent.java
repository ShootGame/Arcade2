package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.objective.ObjectiveEvent;

public class PointEvent extends ObjectiveEvent {
    public PointEvent(Point point) {
        super(point);
    }

    @Override
    public Point getGoal() {
        return (Point) super.getGoal();
    }
}
