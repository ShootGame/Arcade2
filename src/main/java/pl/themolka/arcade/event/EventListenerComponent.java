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

import org.bukkit.event.Listener;
import pl.themolka.arcade.game.GameModule;

import java.util.Collections;
import java.util.List;

public interface EventListenerComponent extends Listener {
    boolean REGISTER_THIS_TOO = true;

    default List<Object> getEventListeners() {
        return Collections.emptyList();
    }

    // This handles only our own event bus.
    @Deprecated
    default void registerEventListeners(EventBus bus) {
        this.registerEventListeners(bus, REGISTER_THIS_TOO);
    }

    // This handles only our own event bus.
    @Deprecated
    default void registerEventListeners(EventBus bus, boolean thisToo) {
        if (thisToo) {
            bus.subscribe(this);
        }

        List<Object> listeners = this.getEventListeners();
        if (listeners != null) {
            for (Object listener : listeners) {
                bus.subscribe(listener);
            }
        }
    }

    default void registerEventListeners(GameModule module) {
        this.registerEventListeners(module, REGISTER_THIS_TOO);
    }

    default void registerEventListeners(GameModule module, boolean thisToo) {
        if (thisToo) {
            module.registerListenerObject(this);
        }

        List<Object> listeners = this.getEventListeners();
        if (listeners != null) {
            for (Object listener : listeners) {
                module.registerListenerObject(listener);
            }
        }
    }

    // This only handles our own event bus.
    @Deprecated
    default void unregisterEventListeners(EventBus bus) {
        this.unregisterEventListeners(bus, REGISTER_THIS_TOO);
    }

    // This only handles our own event bus.
    @Deprecated
    default void unregisterEventListeners(EventBus bus, boolean thisToo) {
        if (thisToo) {
            bus.unsubscribe(this);
        }

        List<Object> listeners = this.getEventListeners();
        if (listeners != null) {
            for (Object listener : listeners) {
                bus.unsubscribe(listener);
            }
        }
    }

    default void unregisterEventListeners(GameModule module) {
        this.unregisterEventListeners(module, REGISTER_THIS_TOO);
    }

    default void unregisterEventListeners(GameModule module, boolean thisToo) {
        if (thisToo) {
            module.unregisterListenerObject(this);
        }

        List<Object> listeners = this.getEventListeners();
        if (listeners != null) {
            for (Object listener : listeners) {
                module.unregisterListenerObject(listener);
            }
        }
    }
}
