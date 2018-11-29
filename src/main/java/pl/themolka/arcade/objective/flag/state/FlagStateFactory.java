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

import pl.themolka.arcade.objective.flag.Flag;
import pl.themolka.arcade.objective.flag.FlagStateEvent;

public class FlagStateFactory {
    private final Flag flag;

    public FlagStateFactory(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {
        return this.flag;
    }

    protected <T extends FlagState> T transform(FlagStateEvent event) {
        Flag flag = event.getGoal();
        flag.getPlugin().getEventBus().publish(event);

        FlagState newState = event.getNewState();
        return flag.transform(newState) ? (T) newState : null;
    }
}
