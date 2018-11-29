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

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;

public class Captured extends PointState {
    public Captured(Point point) {
        super(point);
    }

    @Override
    public Color getColor() {
        return this.getObjective().getOwner().getColor();
    }

    @Override
    public FinitePercentage getProgress() {
        return Progressive.DONE;
    }

    @Override
    public void tick(Tick tick) {
        Multimap<Participator, GamePlayer> dominators = tick.getDominators();
        Participator owner = tick.getOwner();

        if (!dominators.isEmpty() && !dominators.containsKey(owner)) {
            this.startLosing(this.getObjective().getStateFactory(), owner, Progressive.DONE);
        }
    }

    public Losing startLosing(PointStateFactory factory, Participator loser, FinitePercentage progress) {
        return factory.newLosing(loser, progress);
    }
}
