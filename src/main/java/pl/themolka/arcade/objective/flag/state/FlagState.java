package pl.themolka.arcade.objective.flag.state;

import pl.themolka.arcade.objective.ObjectiveState;
import pl.themolka.arcade.objective.flag.Flag;
import pl.themolka.arcade.objective.flag.IFlag;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.state.ProgressiveState;

public abstract class FlagState extends ObjectiveState<Flag> implements IFlag {
    public FlagState(Flag flag) {
        super(flag);
    }

    public abstract static class Progressive extends FlagState implements ProgressiveState {
        private final ObjectiveState.Progressive<Flag> progressive;

        public Progressive(Flag flag) {
            super(flag);

            this.progressive = new ObjectiveState.Progressive<Flag>(flag) {
                @Override
                public Time getProgressTime() {
                    return FlagState.Progressive.this.getProgressTime();
                }
            };
        }

        @Override
        public FinitePercentage getProgress() {
            return this.progressive.getProgress();
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.progressive.setProgress(progress);
        }
    }
}
