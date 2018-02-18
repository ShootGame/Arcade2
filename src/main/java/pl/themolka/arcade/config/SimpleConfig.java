package pl.themolka.arcade.config;

import pl.themolka.arcade.util.StringId;

public class SimpleConfig<P extends SimpleConfig> implements Config<P>, StringId {
    protected final String id;
    protected final P parent;

    public SimpleConfig(String id) {
        this(id, null);
    }

    public SimpleConfig(String id, P parent) {
        this.id = id;
        this.parent = parent;
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public P getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }
}
