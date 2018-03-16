package pl.themolka.arcade.dom;

/**
 * Base interface for all DOM contents.
 */
public interface Content extends Selectable {
    default String toShortString() {
        return this.toString();
    }
}
