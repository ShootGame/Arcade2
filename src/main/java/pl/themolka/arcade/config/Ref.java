package pl.themolka.arcade.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.Locatable;
import pl.themolka.arcade.dom.Selection;
import pl.themolka.arcade.util.OptionalProvider;
import pl.themolka.arcade.util.StringId;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * String ID reference to an object.
 */
public class Ref<T> implements OptionalProvider<T>, Locatable {
    public static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_]{3,}$");

    private static final Ref<?> empty = new EmptyRef();

    private final String id;
    private transient Reference<T> provider;
    private Selection location;

    protected Ref(T provider) {
        this(fetchId(Objects.requireNonNull(provider, "provider cannot be null")));

        this.provide(provider);
    }

    protected Ref(String id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    @Override
    public void locate(Selection selection) {
        this.location = selection;
    }

    @Override
    public boolean isSelectable() {
        return this.location != null;
    }

    @Override
    public Optional<T> optional() {
        return this.isProvided() ? Optional.of(this.get()) : Optional.empty();
    }

    @Override
    public Selection select() {
        return this.location;
    }

    public boolean clear() {
        boolean ok = this.isProvided();
        this.provider = null;
        return ok;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ref && this.id.equals(((Ref) obj).id);
    }

    /**
     * @throws NotProvidedException if there is no provider in this reference.
     */
    public T get() throws NotProvidedException {
        if (!this.isProvided()) {
            throw new NotProvidedException(this, "There is no provider object in this reference");
        }

        return this.provider.get();
    }

    public T getIfPresent() {
        return this.getOrDefault(null);
    }

    public T getOrDefault(T def) {
        return this.isProvided() ? this.get() : def;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public boolean isEmpty() {
        return false;
}

    public boolean isProvided() {
        return this.provider != null && this.provider.get() != null;
    }

    public boolean provide(T provider) {
        if (provider != null) {
            this.provider = new WeakReference<>(provider);
            return true;
        } else {
            this.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(this.id)
                .build();
    }

    private static String fetchId(Object provider) {
        if (provider instanceof StringId) {
            String providerId = ((StringId) provider).getId();
            if (providerId != null && !providerId.isEmpty()) {
                return providerId;
            }
        }

        return generateRandomId(provider);
    }

    private static String generateRandomId(Object provider) {
        return "_undefined-" + RandomStringUtils.randomAlphabetic(10) + "-" + provider.hashCode();
    }

    //
    // Instancing
    //

    public static <T> Ref<T> empty() {
        return (Ref<T>) empty;
    }

    public static <T> Ref<T> of(String id) {
        return ofProvided(id, null);
    }

    public static <T> Ref<T> ofProvided(T provider) {
        return new Ref<>(provider);
    }

    public static <T> Ref<T> ofProvided(String id, T provider) {
        Ref<T> ref = new Ref<>(id);
        ref.provide(provider);
        return ref;
    }
}

final class EmptyRef extends Ref<Object> {
    protected EmptyRef() {
        super("EmptyRef");
    }

    @Override
    public boolean clear() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void locate(Cursor start, Cursor end) {
    }

    @Override
    public void locate(Selection selection) {
    }

    @Override
    public Optional<Object> optional() {
        return Optional.empty();
    }

    @Override
    public boolean provide(Object provider) {
        return false;
    }

    @Override
    public Selection select() {
        throw new UnsupportedOperationException("Empty reference may not be selected");
    }
}
