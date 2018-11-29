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
