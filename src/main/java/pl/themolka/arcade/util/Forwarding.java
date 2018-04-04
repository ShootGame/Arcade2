package pl.themolka.arcade.util;

import com.google.common.collect.ForwardingObject;

import java.util.Objects;

/**
 * The {@link ForwardingObject} does not support types... The best way to omit
 * this is to create our own class with will use types.
 * @param <T> The forwarded class type.
 */
public abstract class Forwarding<T> extends ForwardingObject {
    @Override
    protected abstract T delegate();

    @Override
    public String toString() {
        return Objects.toString(this.delegate());
    }
}
