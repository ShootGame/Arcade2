package pl.themolka.arcade.config;

import pl.themolka.arcade.util.StringId;

public interface IConfig<T> extends StringId {
    default String id() {
        return null;
    }

    @Override
    default String getId() {
        return this.id();
    }
}
