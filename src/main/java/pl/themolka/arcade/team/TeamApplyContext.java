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

package pl.themolka.arcade.team;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.MatchApplyContext;

public class TeamApplyContext extends MatchApplyContext {
    private final Team team;

    public TeamApplyContext(Team team) {
        this.team = team;
    }

    @Override
    protected void applyContent(GamePlayer player, EventType event, Content content) {
        switch (event) {
            case MATCH_START:
                for (GamePlayer member : this.team.getOnlineMembers()) {
                    content.apply(member);
                }
                break;

            case PLAYER_PLAY:
            case PLAYER_RESPAWN:
                content.apply(player);
                break;

            default:
                throw new IllegalArgumentException("Illegal event on content.");
        }
    }

    public Team getTeam() {
        return this.team;
    }
}
