package pl.themolka.arcade.config;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * String ID reference to an object.
 */
public class Ref<T> {
    public static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_]{3,}$");

    private final String id;
    private transient Reference<T> definition;

    protected Ref(T definition) {
        this("_undefined-" + RandomStringUtils.randomAlphanumeric(10));
        this.definition = new WeakReference<T>(definition);
    }

    protected Ref(String id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    public boolean clear() {
        boolean ok = this.isDefined();
        this.definition = null;
        return ok;
    }

    public boolean define(T definition) {
        if (definition != null) {
            this.definition = new WeakReference<>(definition);
            return true;
        } else {
            this.clear();
            return false;
        }
    }

    public T get() {
        return this.definition != null ? this.definition.get() : null;
    }

    public String getId() {
        return this.id;
    }

    public boolean isDefined() {
        return this.get() != null;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(this.get());
    }

    //
    // Instancing
    //

    public static <T> Ref<T> of(String id) {
        return ofProvided(id, null);
    }

    public static <T> Ref<T> ofProvided(T definition) {
        return new Ref<>(definition);
    }

    public static <T> Ref<T> ofProvided(String id, T definition) {
        Ref<T> ref = new Ref<>(id);
        ref.define(definition);
        return ref;
    }
}
