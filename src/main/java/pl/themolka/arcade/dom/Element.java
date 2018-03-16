package pl.themolka.arcade.dom;

import pl.themolka.arcade.util.NamedValue;

/**
 * Something that can hold a parent, name, value and its location.
 * This is the base class for all DOM representation classes.
 */
public abstract class Element extends NamedValue<String, String>
                              implements Cloneable, NestedContent<Node> {
    private final Namespace namespace;
    
    public Element(Namespace namespace, String name) {
        super(name);
        this.namespace = namespace != null ? namespace : Namespace.getDefault();
    }

    public Element(Namespace namespace, String name, String value) {
        super(name, value);
        this.namespace = namespace != null ? namespace : Namespace.getDefault();
    }

    @Override
    public Element clone() {
        return (Element) super.clone();
    }

    public Namespace getNamespace() {
        return this.namespace;
    }
}
