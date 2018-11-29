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

import org.bukkit.Location;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.portal.Portal;

/**
 * Representation of a single Score Box which is used to score given amount of
 * points for the {@link pl.themolka.arcade.game.Participator} and respawn the
 * scoring player. We can use portals for all of these executions.
 */
public class ScoreBox extends Portal {
    private final double points;

    protected ScoreBox(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.points = config.points().get();
    }

    // Use canScore(...) instead.
    @Override
    @Deprecated
    public boolean canTeleport(GamePlayer player) {
        return this.canScore(player);
    }

    /*
     * Same as in portals, but observers and such are not allowed
     * to be teleported since they cannot score points.
     */
    public boolean canScore(GamePlayer player) {
        return player.isParticipating() && super.canTeleport(player);
    }

    public double getPoints() {
        return this.points;
    }

    public Location score(Score score, GamePlayer player) {
        if (score == null || player == null || !player.isOnline()) {
            return null;
        }

        ScoreBoxEvent event = new ScoreBoxEvent(this.getPlugin(), score, this, player, this.points);
        this.getPlugin().getEventBus().publish(event);

        double points = event.getPoints();
        if (event.isCanceled() || !Score.isValid(points)) {
            return null;
        }

        score.incrementScore(player, points);
        return this.teleport(player);
    }

    public interface Config extends Portal.Config {
        Ref<Double> points();

        @Override
        default ScoreBox create(Game game, Library library) {
            return new ScoreBox(game, library, this);
        }
    }
}
