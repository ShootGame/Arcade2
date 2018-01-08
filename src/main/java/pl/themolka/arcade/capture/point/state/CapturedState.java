package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.List;

public class CapturedState extends PointState.Permanent {
    public CapturedState(CapturingState capturing) {
        this(capturing.point);
    }

    public CapturedState(Point point) {
        super(point);
    }

    @Override
    public PointState copy() {
        return new CapturedState(this.point);
    }

    @Override
    public Color getColor() {
        GoalHolder owner = this.getOwner();
        if (owner != null) {
            return owner.getColor();
        }

        // this should never happen
        return this.point.getNeutralColor();
    }

    @Override
    public double getProgress() {
        return Goal.PROGRESS_SCORED;
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, List<GoalHolder> canCapture, GoalHolder owner) {
        if (dominators.isEmpty()) {
            // nobody on the point
            return;
        }

        if (!canCapture.contains(owner)) {
            // The owner is not dominating the point, begin losing it, or
            // start capturing if the capturing captured mode is enabled.
            // If there are more than one enemies on the point and
            // capturing captured mode is enabled - start losing it.

            List<GoalHolder> enemies = new ArrayList<>();
            for (GoalHolder enemy : canCapture) {
                if (!enemy.equals(owner)) {
                    enemies.add(enemy);
                }
            }

            GoalHolder enemy = null;
            if (enemies.size() == 1) {
                enemy = enemies.get(0);
            }

            if (this.point.isCapturingCapturedEnabled() && enemy != null) {
                this.point.startCapturingCaptured(enemy, Progress.ZERO);
            } else {
                this.point.startLosing(owner, Progress.DONE);
            }
        }
    }

    public GoalHolder getOwner() {
        return this.point.getOwner();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .build();
    }
}
