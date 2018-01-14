package pl.themolka.arcade.capture.wool;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Color;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;

public class WoolPlaceFireworks extends GoalFireworkHandler {
    public WoolPlaceFireworks(boolean enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onWoolPlace(WoolPlaceEvent event) {
        if (this.isEnabled() && !event.isCanceled()) {
            Color color = event.getWool().getColor().getFireworkColor();
            this.fireComplete(event.getPlayer().getBukkit().getLocation(), color);
        }
    }
}
