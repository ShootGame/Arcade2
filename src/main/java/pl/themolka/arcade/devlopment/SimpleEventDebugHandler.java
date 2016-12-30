package pl.themolka.arcade.devlopment;

import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.event.EventDebugHandler;

public class SimpleEventDebugHandler implements EventDebugHandler {
    private final Development development;

    public SimpleEventDebugHandler(Development development) {
        this.development = development;
    }

    @Override
    public void handleEventDebug(Event event) {
        this.development.getPlugin().getLogger().info("Calling event '" + event.getEventName() + "'...");
    }
}
