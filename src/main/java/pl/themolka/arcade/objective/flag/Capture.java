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

package pl.themolka.arcade.objective.flag;

import org.bukkit.Location;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.tracker.PlayerTracker;
import pl.themolka.arcade.region.tracker.PlayerTrackerListener;
import pl.themolka.arcade.region.tracker.RegionTrackerFilter;

public class Capture extends pl.themolka.arcade.objective.Capture implements PlayerTrackerListener {
    private Flag flag;
    private PlayerTracker tracker;

    public Capture(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);
    }

    @Override
    public PlayerTracker getTracker() {
        return this.tracker;
    }

    @Override
    public void onEnter(GamePlayer player, Location enter) {
    }

    @Override
    public void onLeave(GamePlayer player, Location leave) {
    }

    public Flag getFlag() {
        return this.flag;
    }

    public void setup(Flag flag) {
        if (this.flag != null || this.tracker != null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is already defined");
        }

        this.flag = flag;
        this.tracker = new PlayerTracker(flag.getGame(), new RegionTrackerFilter(this.getRegion()), this);
    }

    interface Config extends pl.themolka.arcade.objective.Capture.Config {
        @Override
        default Capture create(Game game, Library library) {
            return new Capture(game, library, this);
        }
    }
}
