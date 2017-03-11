package pl.themolka.arcade.goal;

/**
 * An abstract base class for all goals in games.
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
     * Owner of this {@link Goal}.
     * @return owner of this {@link Goal} or `null` if it doesn't contain an owner.
     */
    default GoalHolder getOwner() {
        return null;
    }

    /**
     * Percentage of progress of this `Goal` to be completed.
     * NOTE: Unlimited loop in `#isCompleted() -> #getProgress()`.
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
     * Check if this `Goal` can be completed by the given `GoalHolder`.
     * @param holder `GoalHolder` to check.
     * @return `true` if the given `GoalHolder` can complete this goal, otherwise `false`.
     */
    boolean isCompletableBy(GoalHolder holder);

    /**
     * Check if this `Goal` is completed.
     * NOTE: Unlimited loop in `#getProgress() -> #isCompleted()`.
     * @return `true` if this `Goal` is completed, otherwise `false`.
     */
    default boolean isCompleted() {
        return this.getProgress() == PROGRESS_SCORED;
    }

    /**
     * Check if this `Goal` is completed by the given `GoalHolder`.
     * @param holder `GoalHolder` to check.
     * @return `true` if the given `Goal` is completed, otherwise `false`.
     */
    default boolean isCompleted(GoalHolder holder) {
        return this.isCompleted() && this.isCompletableBy(holder);
    }

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
     * @param holder `GoalHolder` who completed this `Goal`, may be `null`.
     * @param completed `true` if this `Goal` is completed, `false` if not.
     */
    void setCompleted(GoalHolder holder, boolean completed);
}
