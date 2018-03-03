package pl.themolka.arcade.dom;

public final class ImmutableProperty extends Property {
    protected ImmutableProperty(String name, String value) {
        super(name, value);
    }

    @Override
    public Property clone() {
        return this;
    }

    @Override
    public boolean detach() {
        return false;
    }

    @Override
    public void locate(Cursor start, Cursor end) {
    }

    @Override
    public String setName(String name) {
        return null;
    }

    @Override
    public Node setParent(Node parent) {
        return null;
    }

    @Override
    public String setValue(String value) {
        return null;
    }

    //
    // Instancing
    //

    public static ImmutableProperty of(String name) {
        return of(name, null);
    }

    public static ImmutableProperty of(String name, String value) {
        return new ImmutableProperty(name, value);
    }
}
