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

package pl.themolka.arcade.objective.point;

import net.engio.mbassy.bus.IMessagePublication;
import org.bukkit.Location;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.tracker.PlayerTracker;
import pl.themolka.arcade.region.tracker.PlayerTrackerFilter;
import pl.themolka.arcade.region.tracker.PlayerTrackerListener;

public class Capture extends pl.themolka.arcade.objective.Capture implements PlayerTrackerListener {
    private Point point;
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
        this.publish(new CaptureEvent.Enter(this, player, enter));
    }

    @Override
    public void onLeave(GamePlayer player, Location leave) {
        this.publish(new CaptureEvent.Leave(this, player, leave));
    }

    public Point getPoint() {
        return this.point;
    }

    public void setup(Point point) {
        if (this.point != null || this.tracker != null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is already defined");
        }

        this.point = point;
        this.tracker = new PlayerTracker(point.getGame(), new PlayerTrackerFilter() {
            @Override
            public boolean canTrack(GamePlayer player, Location at) {
                return getFieldStrategy().regionContains(getRegion(), at);
            }
        }, this);
    }

    private IMessagePublication publish(CaptureEvent event) {
        return this.point.getPlugin().getEventBus().publish(event);
    }

    public interface Config extends pl.themolka.arcade.objective.Capture.Config {
        @Override
        default Capture create(Game game, Library library) {
            return new Capture(game, library, this);
        }
    }
}
