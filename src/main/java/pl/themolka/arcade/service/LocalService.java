package pl.themolka.arcade.service;

import pl.themolka.arcade.ArcadePlugin;

import java.util.Objects;

class LocalService implements ServerListener {
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

    boolean isLoaded() {
        return this.instance != null && this.instance.isLoaded();
    }

    void reload(ArcadePlugin plugin) throws IllegalAccessException, InstantiationException {
        if (this.instance != null) {
            try {
                this.unload();
            } catch (IllegalStateException ignored) {
                // We don't care
            }

            this.instance = null;
        }

        this.createNewInstance(plugin);
        this.load();
    }
}
