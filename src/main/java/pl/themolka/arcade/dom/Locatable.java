package pl.themolka.arcade.dom;

/**
 * Something that can be located (and selected).
 */
public interface Locatable extends Selectable {
    void locate(Cursor start, Cursor end);
}
