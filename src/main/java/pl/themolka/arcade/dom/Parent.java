package pl.themolka.arcade.dom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Something that can hold children.
 */
public interface Parent<E> {
    boolean add(Collection<E> children);

    default boolean add(E... children) {
        return children != null && this.add(Arrays.asList(children));
    }

    default E child() {
        return this.firstChild();
    }

    default E child(Iterable<String> names) {
        return this.firstChild(names);
    }

    default E child(String... names) {
        return this.firstChild(names);
    }

    List<E> children();

    List<E> children(Iterable<String> names);

    default List<E> children(String... names) {
        return names != null ? this.children(Arrays.asList(names))
                             : Collections.emptyList();
    }

    int clearChildren();

    E firstChild();

    E firstChild(Iterable<String> names);

    default E firstChild(String... names) {
        return names != null ? this.firstChild(Arrays.asList(names))
                             : null;
    }

    boolean isEmpty();

    E lastChild();

    E lastChild(Iterable<String> names);

    default E lastChild(String... names) {
        return names != null ? this.lastChild(Arrays.asList(names))
                             : null;
    }

    boolean remove(Iterable<E> children);

    default boolean remove(E... children) {
        return children != null && this.remove(Arrays.asList(children));
    }

    boolean removeByName(Iterable<String> children);

    default boolean removeByName(E... children) {
        return children != null && this.remove(Arrays.asList(children));
    }

    void sortChildren(Comparator<? super E> comparator);
}
