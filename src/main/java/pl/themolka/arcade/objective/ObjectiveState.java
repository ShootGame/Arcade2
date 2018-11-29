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

package pl.themolka.arcade.objective;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.state.ProgressiveState;
import pl.themolka.arcade.util.state.State;

import java.util.Objects;

public class ObjectiveState<T extends StatableObjective<?>> implements State {
    private final T objective;

    public ObjectiveState(T t) {
        this.objective = t;
    }

    public T getObjective() {
        return this.objective;
    }

    public abstract static class Progressive<T extends StatableObjective<?>> extends ObjectiveState<T>
                                                                             implements ProgressiveState {
        private FinitePercentage progress;

        public Progressive(T t) {
            this(t, Progressive.ZERO);
        }

        public Progressive(T t, FinitePercentage progress) {
            super(t);

            this.progress = Objects.requireNonNull(progress, "progress cannot be null");
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.setProgress(progress, null);
        }

        @Override
        public FinitePercentage getProgress() {
            return progress;
        }

        public void setProgress(FinitePercentage progress, Participator completer) {
            FinitePercentage old = this.getProgress();
            if (old.equals(progress)) {
                return;
            }

            GoalProgressEvent.call(this.getObjective(), completer, old);
            this.progress = progress;
        }
    }
}
