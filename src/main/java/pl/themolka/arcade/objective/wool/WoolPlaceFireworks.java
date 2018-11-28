package pl.themolka.arcade.objective.wool;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;

public class WoolPlaceFireworks extends GoalFireworkHandler {
    public WoolPlaceFireworks(boolean enabled) {
        super(enabled);
    }

    public WoolPlaceFireworks(Ref<Boolean> enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onWoolPlace(WoolPlaceEvent event) {
        if (this.isEnabled() && !event.isCanceled()) {
            this.fireComplete(event.getPlugin(),
                              event.getCompleter().getBukkit().getLocation(),
                              event.getGoal().getColor().getFireworkColor());
        }
    }
}
