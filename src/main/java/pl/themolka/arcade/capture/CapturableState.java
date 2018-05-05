package pl.themolka.arcade.capture;

import org.apache.commons.lang3.builder.ToStringStyle;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;

public abstract class CapturableState<V extends Capturable, E extends CapturableState> {
    public static final ToStringStyle TO_STRING_STYLE = Capturable.TO_STRING_STYLE;

    protected final CaptureGame game;
    protected final V capturable;

    public CapturableState(CaptureGame game, V capturable) {
        this.game = game;
        this.capturable = capturable;
    }

    public abstract E copy();

    public V getCapturable() {
        return this.capturable;
    }

    public String getStateName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public abstract String toString();

    public interface Progress {
        FinitePercentage ZERO = Goal.PROGRESS_UNTOUCHED;
        FinitePercentage DONE = Goal.PROGRESS_SCORED;

        CaptureGame getGame();

        Goal getGoal();

        FinitePercentage getProgress();

        Time getProgressTime();

        default boolean isProgressPositive() {
            return true;
        }

        default void progress(Time interval) {
            double heartbeatInterval = interval.toMillis();
            double progressTime = this.getProgressTime().toMillis();

            double progressPerHeartbeat = heartbeatInterval / progressTime;
            if (!this.isProgressPositive()) {
                progressPerHeartbeat *= -1;
            }

            FinitePercentage oldProgress = this.getProgress();
            GoalProgressEvent.call(this.getGoal(), oldProgress);

            this.setProgress(Percentage.trim(oldProgress.getValue() + progressPerHeartbeat));
        }

        void setProgress(FinitePercentage progress);
    }
}
