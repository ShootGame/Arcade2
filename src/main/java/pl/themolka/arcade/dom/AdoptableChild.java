package pl.themolka.arcade.dom;

/**
 * Representing an adoptable {@link Child}.
 */
public interface AdoptableChild<T> extends Child<T> {
    T setParent(T parent);
}
