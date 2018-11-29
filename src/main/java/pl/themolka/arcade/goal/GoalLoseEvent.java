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

/**
 * Called when a `Goal` is being lost.
 */
public class GoalLoseEvent extends GoalEvent {
    private final Participator loser;

    private GoalLoseEvent(Goal goal, Participator loser) {
        super(goal);

        this.loser = loser;
    }

    public Participator getLoser() {
        return this.loser;
    }

    public boolean hasLoser() {
        return this.loser != null;
    }

    public static GoalLoseEvent call(Goal goal) {
        return call(goal, null);
    }

    public static GoalLoseEvent call(Goal goal, Participator loser) {
        return goal.getPlugin().getEventBus().postEvent(new GoalLoseEvent(goal, loser));
    }
}
