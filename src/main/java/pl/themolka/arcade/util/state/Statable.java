package pl.themolka.arcade.util.state;

public interface Statable<T extends State> {
    T getState();

    boolean transform(T newState);
}
