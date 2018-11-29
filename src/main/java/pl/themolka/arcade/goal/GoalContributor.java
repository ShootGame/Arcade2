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

package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoalContributor extends GamePlayerSnapshot implements Comparable<GoalContributor> {
    private List<Touch> touches = new ArrayList<>();

    public GoalContributor(GamePlayer source) {
        super(source);
    }

    public GoalContributor(String displayName, boolean participating, String username, UUID uuid) {
        super(displayName, participating, username, uuid);
    }

    @Override
    public int compareTo(GoalContributor o) {
        int compare = Integer.compare(this.getTouches(), o.getTouches());
        if (compare == 0) {
            return this.getUsername().compareToIgnoreCase(o.getUsername());
        }

        return compare;
    }

    public Time getLastTouchTime() {
        if (this.touches.isEmpty()) {
            return null;
        }

        return this.touches.get(this.touches.size() - 1).time;
    }

    public int getTouches() {
        return this.touches.size();
    }

    public void touch() {
        this.touches.add(new Touch(Time.now()));
    }

    public void resetTouches() {
        this.touches.clear();
    }

    private class Touch {
        final Time time;

        Touch(Time time) {
            this.time = time;
        }
    }
}
