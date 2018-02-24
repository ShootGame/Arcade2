package pl.themolka.arcade.dom;

public final class Property extends Element {
    private Node parent;

    private Property(String name) {
        this(name, null); // super would make value null
    }

    private Property(String name, String value) {
        super(name, normalizeValue(value));
    }

    @Override
    public Cursor getLocation() {
        return this.hasLocation() ? this.getParent().getLocation() : null;
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
    public boolean isSelectable() {
        return this.hasLocation();
    }

    // TODO: Implement selections properly.
    @Override
    public Selection select() {
        return this.isSelectable() ? Selection.cursor(this.getLocation()) : null;
    }

    @Override
    public Node setParent(Node parent) {
        Node oldParent = this.parent;
        this.parent = parent;

        return oldParent;
    }

    @Override
    public String setValue(String value) {
        return super.setValue(normalizeValue(value));
    }

    @Override
    public String toString() {
        return this.getName() + "=\"" + (this.hasValue() ? this.getValue() : "") + "\"";
    }

    /**
     * Make sure that value is never null.
     */
    static String normalizeValue(String value) {
        return value != null ? value : "";
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
