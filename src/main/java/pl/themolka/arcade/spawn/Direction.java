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

package pl.themolka.arcade.spawn;

import java.util.Objects;

public class Direction {
    public static final float DEFAULT_YAW = 180F; // north
    public static final float DEFAULT_PITCH = 0F; // forward

    private final float yaw;
    private final float pitch;

    public Direction(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direction direction = (Direction) o;
        return Float.compare(direction.yaw, yaw) == 0 &&
                Float.compare(direction.pitch, pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch);
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public static Direction of(float yaw, float pitch) {
        return new Direction(yaw, pitch);
    }
}
