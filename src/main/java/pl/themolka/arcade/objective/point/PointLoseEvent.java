package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.state.Neutral;
import pl.themolka.arcade.objective.point.state.PointState;

public class PointLoseEvent extends PointStateEvent {
    private final Participator loser;

    public PointLoseEvent(PointState oldState, Neutral newState, Participator loser) {
        super(oldState, newState);

        this.loser = loser;
    }

    @Override
    public Neutral getNewState() {
        return (Neutral) super.getNewState();
    }

    public Participator getLoser() {
        return this.loser;
    }
}
