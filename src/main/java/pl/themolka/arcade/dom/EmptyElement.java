package pl.themolka.arcade.dom;

public final class EmptyElement extends Element {
    private EmptyElement(String name) {
        super(name);
    }

    private EmptyElement(String name, String value) {
        super(name, value);
    }

    @Override
    public boolean detach() {
        return false;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public Selection select() {
        return null;
    }

    @Override
    public String setName(String name) {
        return null;
    }

    @Override
    public Node setParent(Node parent) {
        return null;
    }

    //
    // Instancing
    //

    private static final EmptyElement empty = new EmptyElement(EmptyElement.class.getSimpleName());

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
