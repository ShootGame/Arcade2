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
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;

public abstract class SimpleInteractableGoal extends SimpleGoal implements InteractableGoal {
    protected final GoalContributions contributions = new GoalContributions();

    @Deprecated
    public SimpleInteractableGoal(Game game, Participator owner) {
        super(game, owner);
    }

    protected SimpleInteractableGoal(Game game, IGameConfig.Library library, Config<?> config) {
        super(game, library, config);
    }

    @Override
    public GoalContributions getContributions() {
        return this.contributions;
    }

    @Override
    public boolean isUntouched() {
        return !this.isCompleted() && (super.isUntouched() || this.contributions.isEmpty());
    }

    public interface Config<T extends SimpleInteractableGoal> extends SimpleGoal.Config<T> {
    }
}
