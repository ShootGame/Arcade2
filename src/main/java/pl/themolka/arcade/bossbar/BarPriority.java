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

package pl.themolka.arcade.bossbar;

import pl.themolka.arcade.event.Priority;

/**
 * Bar priorities are same as {@link Priority}. Note that {@link #FIRST} and
 * {@link #LAST} should be reserved for internal use only.
 *
 * By convention, priorities higher than {@link #NORMAL} (eg. {@link #HIGH},
 * {@link #HIGHER} and {@link #HIGHEST}) should be used for static content, or
 * server announcements.
 *
 * By convention, priorities lower than {@link #NORMAL} (eg. {@link #LOW},
 * {@link #LOWER} and {@link #LOWEST}) should be used for dynamical content, or
 * specific game goals and map features.
 */
public class BarPriority extends Priority {
    public static int undefined() {
        return NORMAL;
    }
}
