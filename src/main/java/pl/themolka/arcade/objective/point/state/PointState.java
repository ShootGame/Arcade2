package pl.themolka.arcade.objective.point.state;

import pl.themolka.arcade.objective.ObjectiveState;
import pl.themolka.arcade.objective.point.IPoint;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.state.ProgressiveState;

public abstract class PointState extends ObjectiveState<Point> implements IPoint {
    public PointState(Point point) {
        super(point);
    }

    @Override
    public Color getColor() {
        return this.getObjective().getNeutralColor();
    }

    public abstract static class Progressive extends PointState implements ProgressiveState {
        private final ObjectiveState.Progressive<Point> progressive;

        public Progressive(Point point) {
            super(point);

            this.progressive = new ObjectiveState.Progressive<Point>(point) {
                @Override
                public Time getProgressTime() {
                    return PointState.Progressive.this.getProgressTime();
                }
            };
        }

        @Override
        public FinitePercentage getProgress() {
            return this.progressive.getProgress();
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.progressive.setProgress(progress);
        }
    }
}
