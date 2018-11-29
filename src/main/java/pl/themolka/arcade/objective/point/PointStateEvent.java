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

package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.objective.point.state.PointState;

public class PointStateEvent extends PointEvent {
    private final PointState oldState;
    private final PointState newState;

    public PointStateEvent(PointState oldState, PointState newState) {
        super(newState.getObjective());

        this.oldState = oldState;
        this.newState = newState;
    }

    public PointState getOldState() {
        return this.oldState;
    }

    public PointState getNewState() {
        return this.newState;
    }
}
