package pl.themolka.arcade.portal;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.session.PlayerMoveEvent;

public class PortalsGame extends GameModule {
    @Override
    public void onEnable() {
    }

    @Handler(priority = Priority.NORMAL)
    public void detectPortal(PlayerMoveEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
    }
}
