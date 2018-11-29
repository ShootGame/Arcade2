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

package pl.themolka.arcade.match;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerVisibilityFilter;

/**
 * {@link PlayerVisibilityFilter}'s implementation in {@link Match}es.
 */
public class MatchVisibilityFilter implements PlayerVisibilityFilter {
    private final Match match;

    public MatchVisibilityFilter(Match match) {
        this.match = match;
    }

    /**
     * Test if the match is running and the given <code>viewer</code> and <code>player</code> are participating.
     * @param viewer Viewer who can or cannot see the <code>player</code>.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if the <code>player</code> is visible, <code>false</code> otherwise.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        // Viewers not in game (observers, or when the match is not
        // in the running state) always see other players. Otherwise
        // participators can only see other participators.

        return !this.isInGame(viewer) || target.isParticipating();
    }

    public Match getMatch() {
        return this.match;
    }

    /**
     * Check if the given viewer is in game. This method simply checks if
     * the current match is running and the player is participating to it.
     */
    private boolean isInGame(GamePlayer player) {
        return this.match.isRunning() && player.isParticipating();
    }
}
