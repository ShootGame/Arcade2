package pl.themolka.arcade.util;

import java.util.Objects;

public class NamedValue<K, V> implements Cloneable, INamedValue<K, V> {
    private K name;
    private V value;

    public NamedValue(K name) {
        this(name, null);
    }

    public NamedValue(K name, V value) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.value = value;
    }

    @Override
    public Object clone() {
        try {
            NamedValue<K, V> clone = (NamedValue<K, V>) super.clone();
            clone.name = this.name;
            clone.value = this.value;
            // ^ I hope that both name and value are primitive...
            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NamedValue) {
            NamedValue that = (NamedValue) obj;
            return  Objects.equals(this.name, that.name) &&
                    Objects.equals(this.value, that.value);
        }

        return false;
    }

    @Override
    public K getName() {
        return this.name;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.value);
    }

    @Override
    public boolean hasValue() {
        return this.value != null;
    }

    @Override
    public K setName(K name) {
        K oldName = this.name;
        if (name != null) {
            this.name = name;
        }

        return oldName;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;

        return oldValue;
    }
}
