package pl.themolka.arcade.util;

public interface Removable<T> extends Applicable<T> {
    @Override
    default void apply(T t) {
        this.remove(t);
    }

    void remove(T t);
}
