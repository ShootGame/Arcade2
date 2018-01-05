package pl.themolka.arcade.goal;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.Game;

/**
 * An abstract base class for all goals in games.
 */
public interface Goal {
    /**
     * This `Goal` is completed in 100%.
     */
    double PROGRESS_SCORED = 1.0D;

    /**
     * This `Goal` is completed in 0%.
     */
    double PROGRESS_UNTOUCHED = 0.0D;

    /**
     * Colored name of this `Goal` displayed on goal results.
     * @return the colored name of this `Goal`.
     */
    String getColoredName();

    /**
     * Game which this `Goal` is inside.
     * @return {@link Game} of this `Goal`.
     */
    Game getGame();

    /**
     * Uncolored name of this `Goal` displayed on goal results.
     * @return the raw name of this `Goal`.
     */
    default String getName() {
        return ChatColor.stripColor(this.getColoredName());
    }

    /**
     * Owner of this {@link Goal}.
     * @return owner of this {@link Goal} or `null` if it doesn't have an
     * owner.
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
     * @param completer `GoalHolder` to check.
     * @return `true` if the given `GoalHolder` can complete this goal,
     * otherwise `false`.
     */
    boolean isCompletableBy(GoalHolder completer);

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
     * @param completer `GoalHolder` to check.
     * @return `true` if the given `Goal` is completed, otherwise `false`.
     */
    default boolean isCompleted(GoalHolder completer) {
        return this.isCompleted() && this.isCompletableBy(completer);
    }

    /**
     * Check if this `Goal` is or was touched. Touched means that `Goal` was
     * interacted, but not completed.
     * @return `true` if his `Goal` is or was touched, otherwise `false`.
     */
    default boolean isTouched() {
        return !this.isUntouched();
    }

    /**
     * Check if this `Goal` is untouched. Untouched means that `Goal` wasn't
     * ever interacted.
     * @return `true` if this `Goal` is untouched, otherwise `false`.
     */
    default boolean isUntouched() {
        return this.getProgress() == PROGRESS_UNTOUCHED;
    }

    /**
     * Check if this `Goal` is visible to user on ex. the scoreboard.
     * @return `true` if this `Goal` is visible, otherwise `false`.
     */
    default boolean isVisible() {
        return true;
    }

    /**
     * Reset this `Goal` state to the default value.
     * @return `true` if reset is supported and was successfully executed,
     * otherwise `false`.
     */
    default boolean reset() {
        return false;
    }

    /**
     * Set completion of this `Goal` to completed or not.
     * @param completer `GoalHolder` who completed this `Goal`, may be `null`.
     * @param completed `true` if this `Goal` is completed, `false` if not.
     */
    void setCompleted(GoalHolder completer, boolean completed);

    //
    // Utilities
    //

    static boolean isCompletableByEveryone() {
        return true;
    }

    static boolean isCompletableByPositive(Goal goal, GoalHolder completer) {
        return isCompletableByPositive(goal.getOwner(), completer);
    }

    static boolean isCompletableByPositive(GoalHolder owner, GoalHolder completer) {
        return owner == null || owner.equals(completer);
    }

    static boolean isCompletableByNegative(Goal goal, GoalHolder completer) {
        return isCompletableByNegative(goal.getOwner(), completer);
    }

    static boolean isCompletableByNegative(GoalHolder owner, GoalHolder completer) {
        return owner == null || !owner.equals(completer);
    }

    static boolean isNotCompletable() {
        return false;
    }
}
