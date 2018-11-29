/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
