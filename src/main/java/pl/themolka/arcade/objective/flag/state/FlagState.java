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

package pl.themolka.arcade.objective.flag.state;

import pl.themolka.arcade.objective.ObjectiveState;
import pl.themolka.arcade.objective.flag.Flag;
import pl.themolka.arcade.objective.flag.IFlag;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.state.ProgressiveState;

public abstract class FlagState extends ObjectiveState<Flag> implements IFlag {
    public FlagState(Flag flag) {
        super(flag);
    }

    public abstract static class Progressive extends FlagState implements ProgressiveState {
        private final ObjectiveState.Progressive<Flag> progressive;

        public Progressive(Flag flag) {
            super(flag);

            this.progressive = new ObjectiveState.Progressive<Flag>(flag) {
                @Override
                public Time getProgressTime() {
                    return FlagState.Progressive.this.getProgressTime();
                }
            };
        }

        @Override
        public FinitePercentage getProgress() {
            return this.progressive.getProgress();
        }

        @Override
        public void setProgress(FinitePercentage progress) {
            this.progressive.setProgress(progress);
        }
    }
}
