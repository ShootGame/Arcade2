package pl.themolka.arcade.objective.point.state;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;

public class Captured extends PointState {
    public Captured(Point point) {
        super(point);
    }

    @Override
    public Color getColor() {
        return this.getObjective().getOwner().getColor();
    }

    @Override
    public FinitePercentage getProgress() {
        return Progressive.DONE;
    }

    @Override
    public void tick(Tick tick) {
        Multimap<Participator, GamePlayer> dominators = tick.getDominators();
        Participator owner = tick.getOwner();

        if (!dominators.isEmpty() && !dominators.containsKey(owner)) {
            this.startLosing(this.getObjective().getStateFactory(), owner, Progressive.DONE);
        }
    }

    public Losing startLosing(PointStateFactory factory, Participator loser, FinitePercentage progress) {
        return factory.newLosing(loser, progress);
    }
}
