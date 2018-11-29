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

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class TeamEditEvent extends GameEvent implements TeamHolder {
    private final Team newState;
    private final Team oldState;
    private final Reason reason;

    public TeamEditEvent(ArcadePlugin plugin, Team newState, Team oldState, Reason reason) {
        super(plugin);

        this.newState = newState;
        this.oldState = oldState;
        this.reason = reason;
    }

    @Override
    public Team getTeam() {
        return this.getNewState();
    }

    public Team getNewState() {
        return this.newState;
    }

    public Team getOldState() {
        return this.oldState;
    }

    public Reason getReason() {
        return this.reason;
    }

    public enum Reason {
        FRIENDLY_FIRE,
        MAX_PLAYERS,
        MIN_PLAYERS,
        PAINT,
        RENAME,
        SLOTS,
        ;
    }
}
