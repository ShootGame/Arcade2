package pl.themolka.arcade.event;

import org.bukkit.event.Cancellable;

/**
 * This interface gives you abilities to cancel the event from being executed.
 */
public interface Cancelable extends Cancellable {
    /**
     * @deprecated use #isCanceled() instead.
     * @see #isCanceled()
     */
    @Deprecated
    @Override
    default boolean isCancelled() {
        return this.isCanceled();
    }

    /**
     * @deprecated use #setCanceled(boolean) instead.
     * @see #setCanceled(boolean)
     */
    @Deprecated
    @Override
    default void setCancelled(boolean cancel) {
        this.setCanceled(cancel);
    }

    /**
     * Check if this event is canceled.
     * @return `true` if is canceled, `false` otherwise.
     */
    boolean isCanceled();

    /**
     * Cancel this event from executing.
     * @param cancel `true` if the vent should be canceled.
     */
    void setCanceled(boolean cancel);
}
