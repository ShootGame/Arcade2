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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class ServiceManager {
    private final ArcadePlugin plugin;

    private final Map<Class<? extends Service>, LocalService> services = new LinkedHashMap<>();
    private boolean closedForNewServices;

    public ServiceManager(ArcadePlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
    }

    public int load() {
        this.closedForNewServices = true;

        int loaded = 0;
        for (LocalService service : this.services.values()) {
            if (service.isLoaded()) {
                continue;
            }

            try {
                service.load();
                loaded++;
            } catch (Throwable th) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not load '" + service + "' service", th);
            }
        }

        return loaded;
    }

    public int unload() {
        int unloaded = 0;
        for (LocalService service : this.services.values()) {
            if (!service.isLoaded()) {
                continue;
            }

            try {
                service.unload();
                unloaded++;
            } catch (Throwable th) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not unload '" + service + "' service", th);
            }
        }

        return unloaded;
    }

    public Service getService(Class<? extends Service> service) {
        LocalService localService = this.services.get(notNull(service));
        return localService != null ? localService.instance : null;
    }

    public Set<Service> getServices() {
        Set<Service> services = new HashSet<>(this.services.size());
        for (LocalService localService : this.services.values()) {
            services.add(localService.instance);
        }

        return services;
    }

    public boolean isRegistered(Service service) {
        return this.isRegistered(notNull(service).getClass());
    }

    public boolean isRegistered(Class<? extends Service> service) {
        return this.services.containsKey(notNull(service));
    }

    public Service registerService(Class<? extends Service> service) {
        if (this.isRegistered(service)) {
            return null;
        } else if (this.closedForNewServices) {
            throw new IllegalStateException("Services cannot be registered right now");
        }

        LocalService localService = new LocalService(service);
        try {
            localService.createNewInstance(this.plugin);
            this.services.put(service, localService);
            return localService.instance;
        } catch (IllegalAccessException | InstantiationException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not register service '" + localService + "'", e);
            return null;
        }
    }

    public void registerServiceGroup(ServiceGroup serviceGroup) {
        if (this.closedForNewServices) {
            throw new IllegalStateException("Services cannot be registered right now");
        }

        for (Class<? extends Service> service : Objects.requireNonNull(serviceGroup).getServices()) {
            this.registerService(service);
        }
    }

    public boolean unregisterService(Class<? extends Service> service) {
        if (!this.isRegistered(service)) {
            return false;
        } else if (this.closedForNewServices) {
            throw new IllegalStateException("Services cannot be unregistered right now");
        }

        LocalService localService = this.services.get(service);
        if (localService == null) {
            // this should never happen
            return false;
        }

        try {
            localService.unload();
        } catch (IllegalArgumentException ignored) {
            // wrong service state, probably not loaded
            return false;
        }

        this.services.remove(service);
        return true;
    }

    public boolean unregisterService(Service service) {
        return this.unregisterService(notNull(service).getClass());
    }

    private static <T> T notNull(T service) {
        return Objects.requireNonNull(service, "service cannot be null");
    }
}
