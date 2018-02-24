package pl.themolka.arcade.dom;

/**
 * Something that can be selected.
 */
public interface Selectable {
    default boolean isSelectable() {
        return select() != null;
    }

    Selection select();
}
