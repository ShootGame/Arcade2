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

import pl.themolka.arcade.dom.Namespace;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ServiceGroup {
    private final Namespace namespace;
    private final Set<Class<? extends Service>> services = new LinkedHashSet<>();

    public ServiceGroup(Namespace namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
    }

    public boolean addService(Class<? extends Service> service) {
        return this.services.add(Objects.requireNonNull(service, "service cannot be null"));
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Set<Class<? extends Service>> getServices() {
        return new LinkedHashSet<>(this.services);
    }

    public boolean removeService(Class<? extends Service> service) {
        return this.services.remove(Objects.requireNonNull(service, "service cannot be null"));
    }
}
