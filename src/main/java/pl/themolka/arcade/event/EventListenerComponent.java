package pl.themolka.arcade.event;

import pl.themolka.arcade.game.GameModule;

import java.util.Collections;
import java.util.List;

public interface EventListenerComponent {
    boolean REGISTER_THIS_TOO = true;

    default List<Object> getEventListeners() {
        return Collections.emptyList();
    }

    // This only handles our own event bus.
    @Deprecated
    default void registerEventListeners(EventBus bus) {
        this.registerEventListeners(bus, REGISTER_THIS_TOO);
    }

    // This only handles our own event bus.
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
