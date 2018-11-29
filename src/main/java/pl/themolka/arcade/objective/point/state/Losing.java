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
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;

import java.util.Objects;

public class Losing extends PointState.Progressive {
    private final Time loseTime;
    private final Participator loser;

    public Losing(Point point, Time loseTime, Participator loser) {
        super(point);

        this.loseTime = Objects.requireNonNull(loseTime, "loseTime cannot be null");
        this.loser = Objects.requireNonNull(loser, "loser cannot be null");
    }

    @Override
    public Color getColor() {
        return this.loser.getColor();
    }

    @Override
    public Time getProgressTime() {
        return this.loseTime;
    }

    @Override
    public boolean isProgressPositive() {
        return false;
    }

    @Override
    public void tick(Tick tick) {
        Point point = this.getObjective();

        Multimap<Participator, GamePlayer> dominators = tick.getDominators();
        if (dominators.keySet().size() == 1 && dominators.containsKey(this.loser)) {
            // The loser is dominating the point - bring it back to him.
            this.startCapturing(point.getStateFactory(), this.loser, this.getProgress());
            return;
        }

        // Progress the state if there are any dominators on the point.
        if (!dominators.isEmpty() && !dominators.containsKey(this.loser)) {
            this.progress(point.getTickInterval());
        }

        if (isLost(this.getProgress())) {
            this.lose(point.getStateFactory(), this.loser, tick.getOwner());
        }
    }

    public Participator getLoser() {
        return this.loser;
    }

    public void lose(PointStateFactory factory, Participator loser, Participator owner) {
        factory.getPoint().loseObjective(owner);
        factory.newNeutral(loser);
    }

    public Capturing startCapturing(PointStateFactory stateFactory, Participator capturer, FinitePercentage progress) {
        return stateFactory.newCapturing(capturer, progress);
    }

    private static boolean isLost(Percentage input) {
        return input.trim().isZero();
    }
}
