package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Child;
import pl.themolka.arcade.util.StringId;

public class SimpleConfig<T extends SimpleConfig<T>> implements Child<T>, IConfig, StringId {
    protected final String id;
    protected final T parent;

    public SimpleConfig(String id) {
        this(id, null);
    }

    public SimpleConfig(String id, T parent) {
        this.id = id;
        this.parent = parent;
    }

    @Override
    public final boolean detach() {
        return false;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }
}
