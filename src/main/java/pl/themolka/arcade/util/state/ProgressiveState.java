package pl.themolka.arcade.util.state;

import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Percentage;
import pl.themolka.arcade.util.Progressive;

public interface ProgressiveState extends State, Progressive.Mutable {
    Time getProgressTime();

    default boolean isProgressPositive() {
        return true;
    }

    default void progress(Time interval) {
        double tickInterval = interval.toMillis();
        double time = this.getProgressTime().toMillis();

        double perTick = tickInterval / time * (this.isProgressPositive() ? 1 : -1);
        this.setProgress(Percentage.trim(this.getProgress().getValue() + perTick));
    }
}
