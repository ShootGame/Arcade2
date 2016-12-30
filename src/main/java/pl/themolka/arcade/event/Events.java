package pl.themolka.arcade.event;

import java.util.ArrayList;
import java.util.List;

public class Events extends pl.themolka.commons.event.Events {
    private final List<EventDebugHandler> debugHandlerList = new ArrayList<>();

    @Override
    public void post(Object event) {
        if (event instanceof Event) {
            if (!this.debugHandlerList.isEmpty()) {
                for (EventDebugHandler handler : this.debugHandlerList) {
                    handler.handleEventDebug((Event) event);
                }
            }

            super.post(event);
        }
    }

    public boolean registerDebugHandler(EventDebugHandler handler) {
        return this.debugHandlerList.add(handler);
    }

    public boolean unregisterDebugHandler(EventDebugHandler handler) {
        return this.debugHandlerList.remove(handler);
    }
}
