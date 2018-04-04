package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;

import java.util.Objects;

public abstract class ConfigurableMatcher<T> extends Matcher<T> {
    private final T value;

    public ConfigurableMatcher(T value) {
        this.value = value;
    }

    @Override
    public boolean matches(T t) {
        return Objects.equals(this.value, t);
    }

    public T getValue() {
        return this.value;
    }

    public interface Config<T extends ConfigurableMatcher<?>, R> extends Matcher.Config<T> {
        Ref<R> value();
    }
}
