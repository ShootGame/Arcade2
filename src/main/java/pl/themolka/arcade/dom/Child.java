package pl.themolka.arcade.dom;

/**
 * Something that can hold a parent.
 */
public interface Child<E> {
    boolean detach();

    E getParent();

    default boolean hasParent() {
        return this.getParent() != null;
    }

    E setParent(E parent);
}
