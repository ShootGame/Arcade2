package pl.themolka.arcade.util;

public interface Progressive {
    FinitePercentage ZERO = FinitePercentage.ZERO;
    FinitePercentage DONE = FinitePercentage.DONE;

    FinitePercentage getProgress();

    interface Mutable extends Progressive {
        void setProgress(FinitePercentage progress);
    }
}
