package pl.themolka.arcade.event;

import net.engio.mbassy.bus.MBassador;
import pl.themolka.arcade.ArcadePlugin;

public class EventBus extends MBassador<Event> {
    public EventBus(ArcadePlugin plugin) {
        super(new PublicationErrorHandler(plugin.getLogger()));
    }

    public <E extends Event> E postEvent(E event) {
        this.publish(event);
        return event;
    }
}
