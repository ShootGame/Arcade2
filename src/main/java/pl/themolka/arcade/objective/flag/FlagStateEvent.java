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

package pl.themolka.arcade.objective.flag;

import pl.themolka.arcade.objective.flag.state.FlagState;

public class FlagStateEvent extends FlagEvent {
    private final FlagState oldState;
    private final FlagState newState;

    public FlagStateEvent(Flag flag, FlagState oldState, FlagState newState) {
        super(flag);

        this.oldState = oldState;
        this.newState = newState;
    }

    public FlagState getOldState() {
        return this.oldState;
    }

    public FlagState getNewState() {
        return this.newState;
    }
}
