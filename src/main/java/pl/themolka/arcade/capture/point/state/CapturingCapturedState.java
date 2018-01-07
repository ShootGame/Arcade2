package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;

public class CapturingCapturedState extends CapturingState {
    private final LosingState losingState; // never null

    public CapturingCapturedState(CapturedState captured, GoalHolder capturer) {
        this(captured.point, capturer);
    }

    public CapturingCapturedState(Point point, GoalHolder capturer) {
        super(point, capturer);

        this.losingState = new LosingState(this);
    }

    @Override
    public PointState copy() {
        return new CapturingCapturedState(this.point, this.getCapturer());
    }

    @Override
    public ChatColor getColor() {
        PointState.Progress dominating = this.getDominatingState();
        if (dominating instanceof CapturingCapturedState) {
            return super.getColor();
        } else {
            return dominating.getColor();
        }
    }

    @Override
    public Time getProgressTime() {
        return this.point.getCaptureTime();
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        // The point must be first lost to be captured
        this.losingState.heartbeat(ticks, match, competitors, dominators, owner); // losing state
        super.heartbeat(ticks, match, competitors, dominators, owner); // capturing state
    }

    @Override
    public void setProgress(double progress) {
        super.setProgress(progress);
        this.losingState.setProgress(CAPTURED - progress); // reverse
    }

    @Override
    public PointState startLosing(Point point, GoalHolder capturer, Multimap<GoalHolder, GamePlayer> dominators, double progress) {
        if (dominators.size() == 1) {
            // There is only one dominator on the point.
            GoalHolder dominator = new ArrayList<>(dominators.keySet()).get(0);

            if (dominator != null) {
                // Skip losing and directly begin to capture, aka "capturing
                // captured", or if the dominator is the owner, let him
                // capture the point with the normal capture state.

                if (dominator.equals(capturer)) {
                    this.startCapturing(point, capturer, progress);
                } else {
                    this.startCapturingCaptured(point, dominator, progress);
                }
            }
        } else if (!dominators.isEmpty()) {
            // There are more than one dominators on the point.
            super.startLosing(point, capturer, dominators, progress);
        }

        // nobody on the point
        return null;
    }

    public PointState.Progress getDominatingState() {
        // progress < 50%  => losing
        // progress => 50% => capturing
        if (this.getProgress() < CAPTURED / 2D) {
            return this.getLosingState();
        }

        return this;
    }

    public LosingState getLosingState() {
        return this.losingState;
    }

    public PointState startCapturing(Point point, GoalHolder capturer, double progress) {
        return point.startCapturing(capturer, progress);
    }

    public PointState startCapturingCaptured(Point point, GoalHolder capturer, double progress) {
        return point.startCapturingCaptured(capturer, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .append("progress", this.getProgress())
                .append("capturer", this.getCapturer())
                .append("losingState", this.losingState)
                .build();
    }
}
