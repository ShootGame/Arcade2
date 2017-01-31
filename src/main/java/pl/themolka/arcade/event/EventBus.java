package pl.themolka.arcade.event;

import net.engio.mbassy.bus.MBassador;
import pl.themolka.arcade.ArcadePlugin;

public class EventBus extends MBassador<Event> {
    public EventBus(ArcadePlugin plugin) {
        super(new PublicationErrorHandler(plugin.getLogger()));
    }

    public <T extends Event> T postEvent(T event) {
        this.publish(event);
        return event;
    }
}
