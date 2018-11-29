/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.goal;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameHolder;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Progressive;

/**
 * An abstract base class for all goals in games.
 */
public interface Goal extends GameHolder, Progressive {
    /**
     * This `Goal` is completed in 0%.
     */
    FinitePercentage PROGRESS_UNTOUCHED = Progressive.DONE;

    /**
     * This `Goal` is completed in 100%.
     */
    FinitePercentage PROGRESS_SCORED = Progressive.DONE;

    /**
     * Colored name of this `Goal` displayed on goal results.
     * @return the colored name of this `Goal`.
     */
    String getColoredName();

    /**
     * Game which this `Goal` is inside.
     * @return {@link Game} of this `Goal`.
     */
    @Override
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
    default Participator getOwner() {
        return null;
    }

    /**
     * Percentage of progress of this `Goal` to be completed.
     * NOTE: Unlimited loop in `#isCompleted() -> #getProgress()`.
     * @return percentage progress of this `Goal`
     */
    @Override
    default FinitePercentage getProgress() {
        return this.isCompleted() ? PROGRESS_SCORED
                                  : PROGRESS_UNTOUCHED;
    }

    /**
     * Check if this `Goal` can be completed by the given `GoalHolder`.
     * @param completer `GoalHolder` to check.
     * @return `true` if the given `Participator` can complete this goal,
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
     * @param completer `Participator` who completed this `Goal`, may be `null`.
     * @param completed `true` if this `Goal` is completed, `false` if not.
     */
    void setCompleted(boolean completed, Participator completer);

    //
    // "isCompletableBy(...)" Execution Methods
    //

    static boolean completableByEveryone() {
        return true;
    }

    static boolean completableByOwner(Goal goal, GoalHolder completer) {
        return completableByOwner(goal.getOwner(), completer);
    }

    static boolean completableByOwner(GoalHolder owner, GoalHolder completer) {
        return owner == null || owner.equals(completer);
    }

    static boolean completableByNonOwner(Goal goal, GoalHolder completer) {
        return completableByNonOwner(goal.getOwner(), completer);
    }

    static boolean completableByNonOwner(GoalHolder owner, GoalHolder completer) {
        return owner == null || !owner.equals(completer);
    }

    static boolean notCompletable() {
        return false;
    }
}
