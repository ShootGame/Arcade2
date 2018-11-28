package pl.themolka.arcade.objective.core;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;
import pl.themolka.arcade.util.Color;

public class CoreLeakFireworks extends GoalFireworkHandler {
    // We are unable to fire fireworks based on participator's color.
    public static final Color FIREWORK_COLOR = Color.WHITE;

    public CoreLeakFireworks(boolean enabled) {
        super(enabled);
    }

    public CoreLeakFireworks(Ref<Boolean> enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onCoreLeak(CoreLeakEvent event) {
        if (this.isEnabled() && !event.isCanceled()) {
            Plugin plugin = event.getPlugin();

            for (Location at : this.getRegionCorners(event.getGoal().getRegion().getBounds())) {
                this.fireComplete(plugin, at, FIREWORK_COLOR);
            }
        }
    }
}
