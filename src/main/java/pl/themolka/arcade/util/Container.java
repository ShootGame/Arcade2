package pl.themolka.arcade.util;

/**
 * Simple base for all container-oriented classes.
 * @param <T> The type of stored <code>Object</code>s in this container.
 */
public interface Container<T> {
    Class<T> getType();
}
