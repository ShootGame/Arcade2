package pl.themolka.arcade.objective;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.state.ProgressiveState;
import pl.themolka.arcade.util.state.State;

import java.util.Objects;

public class ObjectiveState<T extends StatableObjective<?>> implements State {
    private final T objective;

    public ObjectiveState(T t) {
        this.objective = t;
    }

    public T getObjective() {
        return this.objective;
    }

    public abstract static class Progressive<T extends StatableObjective<?>> extends ObjectiveState<T>
                                                                             implements ProgressiveState {
        private FinitePercentage progress;

        public Progressive(T t) {
            this(t, Progressive.ZERO);
        }

        public Progressive(T t, FinitePercentage progress) {
            super(t);

            this.progress = Objects.requireNonNull(progress, "progress cannot be null");
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.setProgress(progress, null);
        }

        @Override
        public FinitePercentage getProgress() {
            return progress;
        }

        public void setProgress(FinitePercentage progress, Participator completer) {
            FinitePercentage old = this.getProgress();
            if (old.equals(progress)) {
                return;
            }

            GoalProgressEvent.call(this.getObjective(), completer, old);
            this.progress = progress;
        }
    }
}
