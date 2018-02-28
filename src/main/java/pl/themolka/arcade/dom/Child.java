package pl.themolka.arcade.dom;

/**
 * Something that can hold a parent.
 */
public interface Child<T> {
    boolean detach();

    T getParent();

    default boolean hasParent() {
        return this.getParent() != null;
    }
}
