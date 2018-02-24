package pl.themolka.arcade.dom;

/**
 * Something that can be located.
 */
public interface Locatable {
    Cursor getLocation();

    default boolean hasLocation() {
        return this.getLocation() != null;
    }
}
