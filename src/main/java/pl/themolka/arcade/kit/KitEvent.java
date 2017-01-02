package pl.themolka.arcade.kit;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class KitEvent extends Event {
    private final Kit kit;

    public KitEvent(ArcadePlugin plugin, Kit kit) {
        super(plugin);

        this.kit = kit;
    }

    public Kit getKit() {
        return this.kit;
    }
}
