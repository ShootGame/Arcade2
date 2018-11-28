package pl.themolka.arcade.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class RefContainer implements Iterable<Ref<?>> {
    private final Map<String, Ref<?>> byId = new HashMap<>();

    @Override
    public Iterator<Ref<?>> iterator() {
        return new ArrayList<>(this.byId.values()).iterator();
    }

    public int clear() {
        int size = this.byId.size();
        this.byId.clear();
        return size;
    }

    public boolean contains(String id) {
        return this.byId.containsKey(this.validateId(id));
    }

    public boolean contains(Ref<?> ref) {
        return this.contains(this.validateRef(ref).getId());
    }

    public boolean register(Ref<?> ref) {
        return this.byId.putIfAbsent(this.validateRef(ref).getId(), ref) == null;
    }

    public void registerMany(Iterable<Ref<?>> refs) {
        for (Ref<?> ref : refs) {
            this.register(ref);
        }
    }

    public void registerMany(Ref<?>... refs) {
        this.registerMany(Arrays.asList(refs));
    }

    public boolean unregister(String id) {
        return this.byId.remove(this.validateId(id)) != null;
    }

    public boolean unregister(Ref<?> ref) {
        return this.unregister(this.validateRef(ref).getId());
    }

    public Ref<?> query(String id) {
        return this.query(id, null);
    }

    public Ref<?> query(String id, Ref<?> def) {
        return this.byId.getOrDefault(this.validateId(id), def);
    }

    //
    // Validating Inputs
    //

    private String validateId(String id) {
        return Objects.requireNonNull(id, "id cannot be null");
    }

    private <T> Ref<T> validateRef(Ref<T> ref) {
        return Objects.requireNonNull(ref, "ref cannot be null");
    }
}
