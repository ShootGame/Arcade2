package pl.themolka.arcade.goal;

import pl.themolka.arcade.match.MatchWinner;

public interface Goal {
    double PROGRESS_SCORED = 1D;
    double PROGRESS_UNTOUCHED = 0D;

    String getName();

    /**
     * Unlimited loop in #isScored() -> #getProgress()
     */
    default double getProgress() {
        if (this.isScored()) {
            return PROGRESS_SCORED;
        } else {
            return PROGRESS_UNTOUCHED;
        }
    }

    boolean isScorableBy(MatchWinner winner);

    /**
     * Unlimited loop in #getProgress() -> #isScored()
     */
    default boolean isScored() {
        return this.getProgress() == PROGRESS_SCORED;
    }

    boolean isScored(MatchWinner winner);

    default boolean isTouched() {
        return !this.isUntouched();
    }

    default boolean isUntouched() {
        return this.getProgress() == PROGRESS_UNTOUCHED;
    }

    void setScored(MatchWinner winner, boolean scored);
}
