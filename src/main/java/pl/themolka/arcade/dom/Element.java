package pl.themolka.arcade.dom;

import pl.themolka.arcade.util.NamedValue;

/**
 * Something that can hold a parent, name, value and its location.
 * This is the base class for all DOM representation classes.
 */
public abstract class Element extends NamedValue<String, String>
                              implements Cloneable, NestedContent<Node> {
    public Element(String name) {
        super(name);
    }

    public Element(String name, String value) {
        super(name, value);
    }

    @Override
    public Element clone() {
        try {
            return (Element) super.clone();
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    public String toShortString() {
        return this.toString();
    }
}
