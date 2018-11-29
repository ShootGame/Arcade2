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

package pl.themolka.arcade.environment;

import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Node;

public class Environment extends SimpleEnvironmentListener implements Listener {
    public static final EnvironmentType DEFAULT_TYPE = EnvironmentType.DEVELOPMENT;

    private ArcadePlugin plugin;

    private boolean loaded = false;
    private final Node options;
    private final EnvironmentType type;

    public Environment(Node options, EnvironmentType type) {
        this.options = options;
        this.type = type;
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        plugin.getLogger().info("This server is starting in the " + this.type.toString() + " mode...");

        plugin.registerCommandObject(this);
        plugin.registerListenerObject(this);
    }

    public Node getOptions() {
        return this.options;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public EnvironmentType getType() {
        return this.type;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
