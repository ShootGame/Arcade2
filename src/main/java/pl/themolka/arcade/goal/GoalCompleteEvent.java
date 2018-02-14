package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Participator;

/**
 * Called when a `Goal` is being completed.
 */
public class GoalCompleteEvent extends GoalEvent {
    private final Participator completer;

    private GoalCompleteEvent(ArcadePlugin plugin, Goal goal, Participator completer) {
        super(plugin, goal);

        this.completer = completer;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public static GoalCompleteEvent call(ArcadePlugin plugin, Goal goal) {
        return call(plugin, goal, null);
    }

    public static GoalCompleteEvent call(ArcadePlugin plugin, Goal goal, Participator completer) {
        return plugin.getEventBus().postEvent(new GoalCompleteEvent(plugin, goal, completer));
    }
}
