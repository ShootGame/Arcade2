package pl.themolka.arcade.util;

public interface Statable<T extends State> {
    T getState();

    boolean transform(T newState);
}
