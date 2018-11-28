package pl.themolka.arcade.objective.point;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.util.Color;

public class PointCaptureFireworks extends GoalFireworkHandler {
    public PointCaptureFireworks(boolean enabled) {
        super(enabled);
    }

    public PointCaptureFireworks(Ref<Boolean> enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onPointCaptured(PointCaptureEvent event) {
        if (this.isEnabled()) {
            Plugin plugin = event.getPlugin();
            Point point = event.getGoal();
            Region region = point.getCapture().getRegion();
            Color color = event.getCapturer().getColor();

            for (Location at : this.getRegionCorners(region.getBounds())) {
                this.fireComplete(plugin, at, color);
            }
        }
    }
}
