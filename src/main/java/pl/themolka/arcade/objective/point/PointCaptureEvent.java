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

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.state.Captured;
import pl.themolka.arcade.objective.point.state.PointState;

public class PointCaptureEvent extends PointStateEvent {
    private final Participator oldOwner;
    private final Participator capturer;

    public PointCaptureEvent(PointState oldState, Captured newState, Participator oldOwner, Participator capturer) {
        super(oldState, newState);

        this.oldOwner = oldOwner;
        this.capturer = capturer;
    }

    @Override
    public Captured getNewState() {
        return (Captured) super.getNewState();
    }

    public Participator getOldOwner() {
        return this.oldOwner;
    }

    public Participator getCapturer() {
        return this.capturer;
    }

    public boolean hadOwner() {
        return this.oldOwner != null;
    }
}
