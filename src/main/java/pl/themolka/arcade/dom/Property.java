package pl.themolka.arcade.dom;

public class Property extends Element {
    private Node parent;

    private Property(String name) {
        super(name);
    }

    private Property(String name, String value) {
        super(name, value);
    }

    @Override
    public Cursor getLocation() {
        return this.hasLocation() ? this.getParent().getLocation()
                                  : null;
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public boolean hasLocation() {
        return this.hasParent() && this.getParent().hasLocation();
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Override
    public Node setParent(Node parent) {
        Node oldParent = this.parent;
        this.parent = parent;

        return oldParent;
    }

    @Override
    public String toString() {
        return this.getName() + "=\"" + (this.hasValue() ? this.getValue() : "") + "\"";
    }

    //
    // Instancing
    //

    public static Property of(String name) {
        return new Property(name);
    }

    public static Property of(String name, String value) {
        return new Property(name, value);
    }
}
