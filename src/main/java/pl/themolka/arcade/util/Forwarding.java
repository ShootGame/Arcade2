package pl.themolka.arcade.util;

import com.google.common.collect.ForwardingObject;

/**
 * The {@link ForwardingObject} does not support types... The best way to omit
 * this is to create our own class with will use types.
 * @param <V> The forwarded class type.
 */
public abstract class Forwarding<V> extends ForwardingObject {
    @Override
    protected abstract V delegate();
}
