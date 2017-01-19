package pl.themolka.arcade.event;

public interface Cancelable {
    boolean isCanceled();

    void setCanceled(boolean cancel);
}
