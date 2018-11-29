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

package pl.themolka.arcade.event;

import org.bukkit.Server;
import pl.themolka.arcade.ArcadePlugin;

/**
 * Base class for all events in Arcade.
 */
public abstract class Event {
    private final ArcadePlugin plugin;

    public Event(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public String getEventName() {
        return this.getClass().getSimpleName();
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public boolean isAsync() {
        return false;
    }
}
