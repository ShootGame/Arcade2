package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;

public class GoalCreateEvent extends GoalEvent {
    public GoalCreateEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }
}
