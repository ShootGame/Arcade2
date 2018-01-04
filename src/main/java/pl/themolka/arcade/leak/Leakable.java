package pl.themolka.arcade.leak;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalContributionContext;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.InteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Leakable implements InteractableGoal, StringId {
    public static final String DEFAULT_GOAL_NAME = "Leakable";

    protected final LeakGame game;

    private final GoalHolder owner;
    private final GoalContributionContext contributions;
    private final String id;
    private boolean leaked;
    private String name;

    public Leakable(LeakGame game, GoalHolder owner, String id) {
        this.game = game;

        this.owner = owner;
        this.contributions = new GoalContributionContext();
        this.id = id;
        this.leaked = false;
    }

    @Override
    public String getColoredName() {
        return this.owner.getColor().toChat() + this.getName();
    }

    @Override
    public GoalContributionContext getContributions() {
        return this.contributions;
    }

    @Override
    public Game getGame() {
        return this.game.getGame();
    }

    @Override
    public abstract String getGoalInteractMessage(String interact);

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        if (this.hasName()) {
            return this.name;
        }

        return DEFAULT_GOAL_NAME;
    }

    @Override
    public GoalHolder getOwner() {
        return this.owner;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.isCompletableByNegative(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return this.isLeaked();
    }

    @Override
    public boolean reset() {
        if (!this.isLeaked()) {
            return false;
        }

        LeakableResetEvent event = new LeakableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.leaked = false;
            this.contributions.clearContributors();
            this.resetLeakable();
            return true;
        }

        return false;
    }

    @Override
    public void setCompleted(GoalHolder completer, boolean completed) {
        if (completed) {
            this.handleGoalComplete(completer);
        } else {
            this.reset();
        }
    }

    public boolean isLeaked() {
        return this.leaked;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public abstract void leak();

    @Deprecated
    public abstract void resetLeakable();

    public void setName(String name) {
        this.name = name;
    }

    private void handleGoalComplete(GoalHolder completer) {
        if (this.leaked) {
            return;
        }
        this.leaked = true;

        LeakableLeakEvent event = new LeakableLeakEvent(this.game.getPlugin(), this, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            // This game for this `GoalHolder` has been completed - we can tell
            // it to the plugin, so it can end the game. This method will loop
            // all `GameHolder`s (like players or teams) to find the winner.
            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
        }
    }
}
