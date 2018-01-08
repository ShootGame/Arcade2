package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.capture.point.PointCapturedEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;

import java.util.List;

public class CapturingState extends PointState.Progress {
    public static final double CAPTURED = Progress.DONE;

    private final GoalHolder capturer; // this must be final, never null

    public CapturingState(LosingState losing, GoalHolder capturer) {
        this(losing.point, capturer);
    }

    public CapturingState(NeutralState neutral, GoalHolder capturer) {
        this(neutral.point, capturer);
    }

    public CapturingState(Point point, GoalHolder capturer) {
        super(point);

        this.capturer = capturer;
    }

    @Override
    public PointState copy() {
        return new CapturingState(this.point, this.capturer);
    }

    @Override
    public Color getColor() {
        return this.capturer.getColor();
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, List<GoalHolder> canCapture, GoalHolder owner) {
        if (!dominators.isEmpty()) {
            if (!canCapture.contains(this.capturer)) {
                // The dominator has changed.
                this.startLosing(this.point, this.capturer, dominators, this.getProgress());
                return;
            }

            if (canCapture.size() == 1) {
                // Progress the state if there is only one dominator who can dominate the point.
                this.progress();
            }
        }

        if (this.getProgress() >= CAPTURED) { // The point is captured at 100%.
            if (owner != null && owner.equals(this.capturer)) {
                return;
            }

            CapturedState capturedState = this.point.createCapturedState(this);

            PointCapturedEvent event = new PointCapturedEvent(this.game.getPlugin(), this.point, this, capturedState, owner, this.capturer);
            this.game.getPlugin().getEventBus().publish(event);

            if (event.isCanceled()) {
                return;
            }

            PointState newState = event.getNewState();
            this.point.setState(newState);

            if (newState instanceof CapturedState) {
                GoalHolder oldOwner = event.getOldOwner();
                GoalHolder newOwner = event.getNewOwner();

                if (newOwner != null && (oldOwner == null || !oldOwner.equals(newOwner))) {
                    this.point.capture(newOwner, null);
                }
            }
        }
    }

    @Override
    public Time getProgressTime() {
        return this.point.getCaptureTime();
    }

    @Override
    public boolean isProgressPositive() {
        return true;
    }

    public GoalHolder getCapturer() {
        return this.capturer;
    }

    public PointState startLosing(Point point, GoalHolder capturer, Multimap<GoalHolder, GamePlayer> dominators, double progress) {
        return point.startLosing(capturer, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .append("progress", this.getProgress())
                .append("capturer", this.capturer)
                .build();
    }
}
