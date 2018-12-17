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

package pl.themolka.arcade.service;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;

import java.util.Objects;

/**
 * This is a new concept of modularity of the Arcade plugin. Services are small
 * independent by default pieces of the core logic, which (I hope) will make the
 * code more organized and easier to develop in the future.
 * Services should be able to be loaded and unloaded at runtime without any side
 * effects. I want to make the services being categorized in different groups,
 * so they 'll be easier to manage in the future. Services should somehow be
 * configurable (using XML structure), but I don't know how exactly yet.
 * - service = separate XML file
 * - services.xml with all the configurations
 * Not all services are configurable, what should we do with them?
 * Server admins should have a way to disable any of the service.
 * There should be a wait to install third party services from outside.
 */
public abstract class Service implements ServiceListener, Listener {
    private ArcadePlugin plugin;

    private String id;
    private State state;

    enum State {
        NEW, INITIALIZED, LOADED, UNLOADED
    }

    public Service() {
        this.state = State.NEW;
    }

    public final void initalize(String id, ArcadePlugin plugin) {
        if (!this.state.equals(State.NEW)) {
            throw new IllegalStateException();
        }

        this.id = id;
        this.plugin = plugin;

        this.state = State.INITIALIZED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public final void load() {
        if (!this.state.equals(State.INITIALIZED)) {
            throw new IllegalStateException();
        }

        this.loadService();
        this.plugin.registerListenerObject(this);

        this.state = State.LOADED;
    }

    @Override
    public final void unload() {
        if (!this.state.equals(State.LOADED)) {
            throw new IllegalStateException();
        }

        this.plugin.unregisterListenerObject(this);
        this.unloadService();

        this.state = State.UNLOADED;
    }

    protected void loadService() {
    }

    protected void unloadService() {
    }

    public String getId() {
        return this.id;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.plugin.getServer();
    }

    public boolean isLoaded() {
        return this.state.equals(State.LOADED);
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
