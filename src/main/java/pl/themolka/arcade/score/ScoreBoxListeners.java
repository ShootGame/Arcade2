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

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.session.PlayerMoveEvent;

public class ScoreBoxListeners {
    private final ScoreGame game;

    public ScoreBoxListeners(ScoreGame game) {
        this.game = game;
    }

    @Handler(priority = Priority.LOWEST)
    public void onPlayerEnterScoreBox(PlayerMoveEvent event) {
        if (event.isCanceled() || !this.game.hasAnyScoreBoxes()) {
            return;
        }

        GamePlayer player = event.getGamePlayer();
        ScoreBox scoreBox = this.game.getScoreBox(event.getTo().toVector());

        if (scoreBox == null || !scoreBox.canScore(player)) {
            return;
        }

        Participator participator = this.game.getMatch().findWinnerByPlayer(player);
        if (participator != null) {
            Score score = this.game.getScore(participator);
            if (score != null) {
                scoreBox.score(score, player);
            }
        }
    }
}
