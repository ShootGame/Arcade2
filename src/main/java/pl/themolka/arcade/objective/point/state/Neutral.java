package pl.themolka.arcade.objective.point.state;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;

import java.util.Set;

public class Neutral extends PointState {
    public Neutral(Point point) {
        super(point);
    }

    @Override
    public Color getColor() {
        return this.getObjective().getNeutralColor();
    }

    @Override
    public FinitePercentage getProgress() {
        return Progressive.ZERO;
    }

    @Override
    public void tick(Tick tick) {
        Set<Participator> dominators = tick.getDominators().keySet();
        if (dominators.size() == 1) {
            for (Participator dominator : dominators) {
                this.startCapturing(this.getObjective().getStateFactory(), dominator, Progressive.ZERO);
                break;
            }
        }
    }

    public Capturing startCapturing(PointStateFactory factory, Participator capturer, FinitePercentage progress) {
        return factory.newCapturing(capturer, progress);
    }
}
