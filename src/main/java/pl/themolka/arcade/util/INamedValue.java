package pl.themolka.arcade.util;

import java.util.Map;

/**
 * Something that holds a name and a value.
 */
public interface INamedValue<K, V> extends Map.Entry<K, V> {
    /**
     * @deprecated Use {@link #getName()} instead.
     */
    @Override
    @Deprecated
    default K getKey() {
        return this.getName();
    }

    K getName();

    @Override
    V getValue();

    boolean hasValue();

    K setName(K name);

    @Override
    V setValue(V value);
}
