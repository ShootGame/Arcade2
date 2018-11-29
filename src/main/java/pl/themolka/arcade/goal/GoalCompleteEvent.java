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
 * Called when a `Goal` is being completed.
 */
public class GoalCompleteEvent extends GoalEvent {
    private final Participator completer;

    private GoalCompleteEvent(Goal goal, Participator completer) {
        super(goal);

        this.completer = completer;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public static GoalCompleteEvent call(Goal goal) {
        return call(goal, null);
    }

    public static GoalCompleteEvent call(Goal goal, Participator completer) {
        return goal.getPlugin().getEventBus().postEvent(new GoalCompleteEvent(goal, completer));
    }
}
