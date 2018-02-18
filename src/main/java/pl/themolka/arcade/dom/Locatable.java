package pl.themolka.arcade.dom;

/**
 * Something that is located.
 */
public interface Locatable {
    Cursor getLocation();

    default boolean hasLocation() {
        return this.getLocation() != null;
    }
}
