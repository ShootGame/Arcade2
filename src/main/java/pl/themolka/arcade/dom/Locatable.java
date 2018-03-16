package pl.themolka.arcade.dom;

import java.util.Objects;

/**
 * Something that can be located (and selected).
 */
public interface Locatable extends Selectable {
    default void locate(Cursor start, Cursor end) {
        this.locate(Selection.between(start, end));
    }

    default void locate(Selection selection) {
        Objects.requireNonNull(selection, "selection cannot be null");
        this.locate(selection.getStart(), selection.getEnd());
    }
}
