package pl.themolka.arcade.portal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class PortalEvent extends GameEvent {
    private final Portal portal;

    public PortalEvent(ArcadePlugin plugin, Portal portal) {
        super(plugin);

        this.portal = portal;
    }

    public Portal getPortal() {
        return this.portal;
    }
}
