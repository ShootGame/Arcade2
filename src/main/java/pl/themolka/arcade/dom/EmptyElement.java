package pl.themolka.arcade.dom;

public final class EmptyElement extends Element {
    private EmptyElement(String name) {
        super(name);
    }

    private EmptyElement(String name, String value) {
        super(name, value);
    }

    @Override
    public Cursor getLocation() {
        return null;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public boolean hasLocation() {
        return false;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public Node setParent(Node parent) {
        return null;
    }

    //
    // Instancing
    //

    private static final EmptyElement empty = new EmptyElement("__empty__");

    public static EmptyElement empty() {
        return empty;
    }

    public static EmptyElement of(String name) {
        return new EmptyElement(name);
    }

    public static EmptyElement of(String name, String value) {
        return new EmptyElement(name, value);
    }
}
