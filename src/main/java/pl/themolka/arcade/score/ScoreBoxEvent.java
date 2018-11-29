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

package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;

public class ScoreBoxEvent extends ScoreEvent implements Cancelable {
    private boolean cancel;
    private final ScoreBox scoreBox;
    private final GamePlayer player;
    private double points;

    public ScoreBoxEvent(ArcadePlugin plugin, Score score, ScoreBox scoreBox, GamePlayer player, double points) {
        super(plugin, score);

        this.scoreBox = scoreBox;
        this.player = player;
        this.points = points;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public ScoreBox getScoreBox() {
        return this.scoreBox;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public double getPoints() {
        return this.points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
