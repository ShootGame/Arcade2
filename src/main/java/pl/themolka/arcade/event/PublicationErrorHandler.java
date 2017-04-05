package pl.themolka.arcade.event;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicationErrorHandler implements IPublicationErrorHandler {
    private final Logger logger;

    public PublicationErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleError(PublicationError error) {
        this.logger.log(Level.SEVERE, "Could not handle event '" +
                ((Event) error.getPublishedMessage()).getEventName() +
                "' in " + error.getListener().getClass().getName() +
                " because: " + error.getMessage(), error.getCause());
    }
}
