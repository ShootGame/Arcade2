package pl.themolka.arcade.util;

import com.google.common.collect.ForwardingObject;

/**
 * The {@link ForwardingObject} does not support types... The best way to omit
 * this is to create our own class with will use types.
 * @param <T> The forwarded class type.
 */
public abstract class Forwarding<T> extends ForwardingObject {
    @Override
    protected abstract T delegate();
}
