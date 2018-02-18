package pl.themolka.arcade.dom;

/**
 * Something that its location can be changed.
 */
public interface MutableLocatable extends Locatable {
    void setLocation(Cursor location);
}
