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

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;

public class NullGoal implements Goal {
    public static final String GOAL_NAME = "Null Goal";

    private final Game game;

    public NullGoal(Game game) {
        this.game = game;
    }

    @Override
    public String getColoredName() {
        return this.getName();
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public String getName() {
        return GOAL_NAME;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.notCompletable();
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void setCompleted(boolean completed, Participator completer) {
    }
}
