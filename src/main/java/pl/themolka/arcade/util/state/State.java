package pl.themolka.arcade.util.state;

public interface State {
    default String getStateName() {
        return this.getClass().getSimpleName();
    }

    default void construct() {
    }

    default void destroy() {
    }
}
