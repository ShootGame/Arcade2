package pl.themolka.arcade.util;

public interface State {
    default String getStateName() {
        return this.getClass().getSimpleName();
    }
}
