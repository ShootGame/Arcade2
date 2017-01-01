package pl.themolka.arcade.event;

public class Events extends pl.themolka.commons.event.Events {
    @Override
    public void post(Object event) {
        if (event instanceof Event) {
            super.post(event);
        }
    }
}
