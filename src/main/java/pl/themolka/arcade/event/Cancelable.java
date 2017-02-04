package pl.themolka.arcade.event;

import org.bukkit.event.Cancellable;

public interface Cancelable extends Cancellable {
    @Override
    default boolean isCancelled() {
        return this.isCanceled();
    }

    @Override
    default void setCancelled(boolean cancel) {
        this.setCanceled(cancel);
    }

    boolean isCanceled();

    void setCanceled(boolean cancel);
}
