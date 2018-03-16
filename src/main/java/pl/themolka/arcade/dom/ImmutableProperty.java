package pl.themolka.arcade.dom;

public final class ImmutableProperty extends Property {
    protected ImmutableProperty(Namespace namespace, String name, String value) {
        super(namespace, name, value);
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
    public void locate(Selection selection) {
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

    public static ImmutableProperty of(Namespace namespace, String name) {
        return of(namespace, name, null);
    }

    public static ImmutableProperty of(Namespace namespace, String name, String value) {
        return new ImmutableProperty(namespace, name, value);
    }
}
