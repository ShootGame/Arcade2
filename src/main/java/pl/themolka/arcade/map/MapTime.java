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

package pl.themolka.arcade.map;

import pl.themolka.arcade.time.Time;

public class MapTime implements Cloneable {
    public static final int MIN_TICKS = 0;
    public static final int MAX_TICKS = 23999;

    public static final MapTime MIN = ofTicks(MIN_TICKS);
    public static final MapTime MAX = ofTicks(MAX_TICKS);

    private final long ticks;
    private boolean locked;

    private MapTime(long ticks) {
        this.ticks = ticks;
    }

    @Override
    public MapTime clone() {
        try {
            MapTime clone = (MapTime) super.clone();
            clone.locked = this.locked;
            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    public long getTicks() {
        return this.ticks;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Time toTime() {
        return Time.ofTicks(this.getTicks());
    }

    //
    // Instancing
    //

    public static MapTime ofTicks(long ticks) {
        return new MapTime(ticks);
    }

    public static MapTime defaultTime() {
        return MapTimeConstants.DAY.time().clone();
    }
}
