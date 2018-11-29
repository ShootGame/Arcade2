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

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Progressive;

public class GoalProgressEvent extends GoalEvent implements Progressive {
    private Participator completer;
    private final FinitePercentage oldProgress;
    private final FinitePercentage newProgress;

    private GoalProgressEvent(Goal goal, Participator completer, FinitePercentage oldProgress, FinitePercentage newProgress) {
        super(goal);

        this.completer = completer;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }

    @Override
    public FinitePercentage getProgress() {
        return this.newProgress;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public FinitePercentage getOldProgress() {
        return this.oldProgress;
    }

    public static GoalProgressEvent call(Goal goal, FinitePercentage oldProgress) {
        return call(goal, null, oldProgress);
    }

    public static GoalProgressEvent call(Goal goal, Participator completer, FinitePercentage oldProgress) {
        return goal.getPlugin().getEventBus().postEvent(new GoalProgressEvent(goal, completer, oldProgress, goal.getProgress()));
    }
}
