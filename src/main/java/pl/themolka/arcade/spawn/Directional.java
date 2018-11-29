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

import pl.themolka.arcade.config.IConfig;

public interface Directional {
    default org.bukkit.geometry.Direction createDirection() {
        return org.bukkit.geometry.Direction.of(this.getYaw(), this.getPitch());
    }

    default float getYaw() {
        return Config.DEFAULT_YAW;
    }

    default float getPitch() {
        return Config.DEFAULT_PITCH;
    }

    interface Config extends IConfig {
        float DEFAULT_YAW = 180F; // north
        float DEFAULT_PITCH = 0F; // forward

        default float yaw() { return DEFAULT_YAW; };
        default float pitch() { return DEFAULT_PITCH; };
    }
}
