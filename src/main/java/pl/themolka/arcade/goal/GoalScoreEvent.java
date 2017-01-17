package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.event.Cancelable;

public class GoalScoreEvent extends GoalEvent implements Cancelable {
    private boolean cancel;

    public GoalScoreEvent(ArcadePlugin plugin, Goal goal) {
        super(plugin, goal);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
