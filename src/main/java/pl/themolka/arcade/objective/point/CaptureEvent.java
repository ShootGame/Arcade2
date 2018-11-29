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

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;

public class CaptureEvent extends PointEvent {
    private final Capture capture;
    private final GamePlayer player;
    private final Location location;

    public CaptureEvent(Capture capture, GamePlayer player, Location location) {
        super(capture.getPoint());

        this.capture = capture;
        this.player = player;
        this.location = location;
    }

    public Capture getCapture() {
        return this.capture;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public static class Enter extends CaptureEvent {
        public Enter(Capture capture, GamePlayer player, Location location) {
            super(capture, player, location);
        }
    }

    public static class Leave extends CaptureEvent {
        public Leave(Capture capture, GamePlayer player, Location location) {
            super(capture, player, location);
        }
    }
}
