package pl.themolka.arcade.event;

import net.engio.mbassy.bus.IMessagePublication;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.publication.SyncAsyncPostCommand;
import pl.themolka.arcade.ArcadePlugin;

import java.util.concurrent.TimeUnit;

public class EventBus extends MBassador<Object> {
    private final ArcadePlugin plugin;

    public EventBus(ArcadePlugin plugin) {
        super(new PublicationErrorHandler(plugin.getLogger()));

        this.plugin = plugin;
    }

    @Override
    public IMessagePublication publishAsync(Object message) {
        if (message instanceof Event) {
            return super.publishAsync(message);
        }

        return null;
    }

    @Override
    public IMessagePublication publishAsync(Object message, long timeout, TimeUnit unit) {
        if (message instanceof Event) {
            return super.publishAsync(message, timeout, unit);
        }

        return null;
    }

    @Override
    public IMessagePublication publish(Object message) {
        if (message instanceof Event) {
            return super.publish(message);
        }

        return null;
    }

    @Override
    public SyncAsyncPostCommand<Object> post(Object message) {
        if (message instanceof Event) {
            return super.post(message);
        }

        return null;
    }
}
