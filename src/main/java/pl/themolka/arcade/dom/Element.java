package pl.themolka.arcade.dom;

import pl.themolka.arcade.util.NamedValue;

/**
 * Something that can hold a parent, name, value and its location.
 */
public abstract class Element extends NamedValue<String, String>
                              implements Child<Node>, Locatable {
    public Element(String name) {
        super(name);
    }

    public Element(String name, String value) {
        super(name, value);
    }

    public String toShortString() {
        return this.toString();
    }
}
