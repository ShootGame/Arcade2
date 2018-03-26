package pl.themolka.arcade.config;

import pl.themolka.arcade.util.StringId;

public interface Unique extends StringId {
    String id();

    /**
     * @deprecated Use {@link #id()} instead.
     */
    @Deprecated
    @Override
    default String getId() {
        return this.id();
    }
}
