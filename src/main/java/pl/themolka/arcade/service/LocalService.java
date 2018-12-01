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

import pl.themolka.arcade.ArcadePlugin;

import java.util.Objects;

class LocalService implements ServiceListener {
    final Class<? extends Service> clazz;
    final String id;
    Service instance;

    LocalService(Class<? extends Service> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "clazz cannot be null");

        ServiceId serviceId = clazz.getDeclaredAnnotation(ServiceId.class);
        if (serviceId == null) {
            throw new IllegalStateException(clazz.getSimpleName() + " must be @" +
                    ServiceId.class.getSimpleName() + " decorated");
        }
        this.id = serviceId.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalService that = (LocalService) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public void load() {
        if (this.instance == null) {
            throw new IllegalStateException("Service '" + this + "' not initialized");
        }

        this.instance.load();
    }

    @Override
    public void unload() {
        if (this.instance == null) {
            throw new IllegalStateException("Service '" + this + "' not initialized");
        }

        this.instance.unload();
    }

    @Override
    public String toString() {
        return this.id;
    }

    void createNewInstance(ArcadePlugin plugin) throws IllegalAccessException, InstantiationException {
        if (this.instance != null) {
            // Make sure to not forget about the old instance. Otherwise it
            // could lead to a memory leak, or event dead event listeners.
            throw new IllegalStateException();
        }

        this.instance = this.clazz.newInstance();
        this.instance.initalize(this.id, Objects.requireNonNull(plugin, "plugin cannot be null"));
    }

    void destroyInstance() {
        if (this.isLoaded()) {
            this.unload();
        }

        this.instance = null;
    }

    boolean isLoaded() {
        return this.instance != null && this.instance.isLoaded();
    }

    void restart(ArcadePlugin plugin) throws IllegalAccessException, InstantiationException {
        try {
            if (this.instance != null) {
                this.destroyInstance();
            }
        } catch (IllegalStateException ignored) {
            // We don't care
        } finally {
            this.createNewInstance(plugin);
            this.load();
        }
    }
}
