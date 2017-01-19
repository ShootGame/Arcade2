package pl.themolka.arcade.goal;

import pl.themolka.arcade.match.MatchWinner;

/**
 * An abstract base class for all `Match` goals.
 */
public interface Goal {
    /**
     * This `Goal` is completed in 100%.
     */
    double PROGRESS_SCORED = 1D;

    /**
     * This `Goal` is completed in 0%.
     */
    double PROGRESS_UNTOUCHED = 0D;

    /**
     * Name of this `Goal` displayed on goal results.
     * @return the name of this `Goal`.
     */
    String getName();

    /**
     * Percentage of progress of this `Goal` to be completed.
     * NOTE: Unlimited loop in `#isScored() -> #getProgress()`.
     * @return percentage progress of this `Goal`
     */
    default double getProgress() {
        if (this.isCompleted()) {
            return PROGRESS_SCORED;
        } else {
            return PROGRESS_UNTOUCHED;
        }
    }

    /**
     * Check if this `Goal` can be completed by the given `MatchWinner`.
     * @param winner `MatchWinner` to check.
     * @return `true` if the given `MatchWinner` can complete this goal, otherwise `false`.
     */
    boolean isCompletableBy(MatchWinner winner);

    /**
     * Check if this `Goal` is completed.
     * NOTE: Unlimited loop in `#getProgress() -> #isScored()`.
     * @return `true` if this `Goal` is completed, otherwise `false`.
     */
    default boolean isCompleted() {
        return this.getProgress() == PROGRESS_SCORED;
    }

    /**
     * Check if this `Goal` is completed by the specified `MatchWinner`.
     * @param winner `MatchWinner` to check.
     * @return `true` if the given `Goal` is completed, otherwise `false`.
     */
    boolean isCompleted(MatchWinner winner);

    /**
     * Check if this `Goal` is or was touched. Touched means that `Goal` was interacted, but not completed.
     * @return `true` if his `Goal` is or was touched, otherwise `false`.
     */
    default boolean isTouched() {
        return !this.isUntouched();
    }

    /**
     * Check if this `Goal` is untouched. Untouched means that `Goal` wasn't ever interacted.
     * @return `true` if this `Goal` is untouched, otherwise `false`.
     */
    default boolean isUntouched() {
        return this.getProgress() == PROGRESS_UNTOUCHED;
    }

    /**
     * Reset this `Goal` state to the default value.
     * @return `true` if reset is supported and was successfully executed, otherwise `false`.
     */
    default boolean reset() {
        return false;
    }

    /**
     * Set completion of this `Goal` to completed or not.
     * @param winner `MatchWinner` who completed this `Goal`, may be `null`.
     * @param completed `true` if this `Goal` is completed, `false` if not.
     */
    void setCompleted(MatchWinner winner, boolean completed);
}
