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

package pl.themolka.arcade.objective.point.state;

import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;

import java.util.Set;

public class Neutral extends PointState {
    public Neutral(Point point) {
        super(point);
    }

    @Override
    public Color getColor() {
        return this.getObjective().getNeutralColor();
    }

    @Override
    public FinitePercentage getProgress() {
        return Progressive.ZERO;
    }

    @Override
    public void tick(Tick tick) {
        Set<Participator> dominators = tick.getDominators().keySet();
        if (dominators.size() == 1) {
            for (Participator dominator : dominators) {
                this.startCapturing(this.getObjective().getStateFactory(), dominator, Progressive.ZERO);
                break;
            }
        }
    }

    public Capturing startCapturing(PointStateFactory factory, Participator capturer, FinitePercentage progress) {
        return factory.newCapturing(capturer, progress);
    }
}
