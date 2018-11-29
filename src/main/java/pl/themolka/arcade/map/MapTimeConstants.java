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

/**
 * See https://minecraft.gamepedia.com/Day-night_cycle
 */
public enum MapTimeConstants {
    // day
    SUNRISE(0L),
    MORNING(1000L),
    FORENOON(4000L),
    NOON(6000L), MIDDAY(NOON.ticks()),
    AFTERNOON(8000L),
    EVENING(11000L),
    SUNSET(13000L),

    // night
    MUSK(13800L), // mobs may spawn here

    MOONRISE(14500L),
    MIDNIGHT(18000L),
    MOONSET(21500L),

    DAWN(23000L), // mobs may spawn here

    // extra shortcuts
    DAY(MIDDAY.ticks()),
    NIGHT(MIDNIGHT.ticks()),
    ;

    private final MapTime time;

    MapTimeConstants(long ticks) {
        this.time = MapTime.ofTicks(ticks);
    }

    public long ticks() {
        return this.time.getTicks();
    }

    public MapTime time() {
        return this.time;
    }
}
