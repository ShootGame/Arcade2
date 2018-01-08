package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.util.Color;

import java.util.List;

public class NeutralState extends PointState.Permanent {
    public NeutralState(LosingState losing) {
        this(losing.point);
    }

    public NeutralState(Point point) {
        super(point);
    }

    @Override
    public PointState copy() {
        return new NeutralState(this.point);
    }

    @Override
    public Color getColor() {
        return this.point.getNeutralColor();
    }

    @Override
    public double getProgress() {
        return Goal.PROGRESS_UNTOUCHED;
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, List<GoalHolder> canCapture, GoalHolder owner) {
        // The neutral state can only be captured by one dominator who can capture the point.
        if (canCapture.size() == 1) {
            GoalHolder dominator = canCapture.get(0);

            if (dominator != null) {
                this.startCapturing(this.point, dominator, Progress.ZERO);
            }
        }
    }

    public PointState startCapturing(Point point, GoalHolder capturer, double progress) {
        return point.startCapturing(capturer, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .build();
    }
}
