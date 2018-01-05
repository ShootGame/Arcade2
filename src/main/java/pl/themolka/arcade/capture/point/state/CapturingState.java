package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.capture.point.PointCapturedEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;

public class CapturingState extends PointState.Progress {
    private final GoalHolder capturer; // this must be final

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
    public ChatColor getColor() {
        return this.capturer.getColor().toChat();
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        if (dominators.size() == 1) {
            GoalHolder dominator = new ArrayList<>(dominators.keySet()).get(0);

            if (!this.capturer.equals(dominator)) {
                // The dominator has changed.
                if (this.point.isCapturingCapturedEnabled()) {
                    this.point.startCapturingCaptured(dominator, this.getProgress());
                } else {
                    this.point.startLosing(this.capturer, this.getProgress());
                }
                return;
            }
        }

        // Progress the state if there are any dominators on the point.
        if (!dominators.isEmpty()) {
            this.progress();
        }

        if (this.getProgress() >= DONE) { // the point is captured at 100%
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

                if (newOwner != null && (oldOwner == null || oldOwner.equals(newOwner))) {
                    this.point.capture(newOwner);
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
}
