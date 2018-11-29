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

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;

public abstract class SimpleProgressGoal extends SimpleGoal {
    private FinitePercentage initialProgress;
    private FinitePercentage progress;

    protected SimpleProgressGoal(Game game, IGameConfig.Library library, Config<?> config) {
        super(game, library, config);

        this.initialProgress = config.initialProgress().get();
        this.progress = config.initialProgress().get();
    }

    @Override
    public FinitePercentage getProgress() {
        return this.progress;
    }

    public FinitePercentage getInitialProgress() {
        return this.initialProgress;
    }

    public void setProgress(double progress) {
        this.progress = Percentage.trim(progress);
    }

    public interface Config<T extends SimpleProgressGoal> extends SimpleGoal.Config<T> {
        FinitePercentage DEFAULT_INITIAL_PROGRESS = Goal.PROGRESS_UNTOUCHED;

        default Ref<FinitePercentage> initialProgress() { return Ref.ofProvided(DEFAULT_INITIAL_PROGRESS); }
    }
}
