package pl.themolka.arcade.service;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Namespace;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ServiceGroup implements ServerListener {
    private final Namespace namespace;
    private final Set<Service> services = new LinkedHashSet<>();

    public ServiceGroup(Namespace namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
    }

    @Override
    public void load() {
        for (Service service : this.services) {
            service.load();
        }
    }

    @Override
    public void unload() {
        for (Service service : this.services) {
            service.unload();
        }
    }

    public final void initalize(ArcadePlugin plugin) {
        for (Service service : this.services) {
            service.initalize(plugin);
        }
    }

    public boolean addService(Service service) {
        return this.services.add(Objects.requireNonNull(service, "service cannot be null"));
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Set<Service> getServices() {
        return new LinkedHashSet<>(this.services);
    }

    public boolean removeService(Service service) {
        return this.services.remove(Objects.requireNonNull(service, "service cannot be null"));
    }
}
