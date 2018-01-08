package pl.themolka.arcade.leak;

import org.apache.commons.lang3.builder.ToStringStyle;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalContributionContext;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.InteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Leakable implements InteractableGoal, StringId {
    // The "ToStringStyle.SHORT_PREFIX_STYLE" strings are long and unreadable here.
    public static final ToStringStyle TO_STRING_STYLE = ToStringStyle.MULTI_LINE_STYLE;

    protected final LeakGame game;

    private GoalHolder owner;
    private final GoalContributionContext contributions;
    private final String id;
    protected boolean leaked;
    protected GoalHolder leakedBy;
    private String name;

    public Leakable(LeakGame game, String id) {
        this(game, null, id);
    }

    public Leakable(LeakGame game, GoalHolder owner, String id) {
        this.game = game;

        this.owner = owner;
        this.contributions = new GoalContributionContext();
        this.id = id;
        this.leaked = false;
    }

    @Override
    public String getColoredName() {
        String color = "";
        if (this.hasOwner()) {
            color = this.getOwner().getColor().toChat().toString();
        }

        return color + this.getName();
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

        return this.getDefaultName();
    }

    @Override
    public GoalHolder getOwner() {
        return this.owner;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByNegative(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return this.isLeaked();
    }

    @Override
    public boolean isCompleted(GoalHolder completer) {
        return this.isCompleted() && (this.leakedBy == null || this.leakedBy.equals(completer));
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

            this.contributions.clearContributors();
            this.leaked = false;
            this.leakedBy = null;
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

    public abstract String getDefaultName();

    public GoalHolder getLeakedBy() {
        return this.leakedBy;
    }

    public LeakGame getLeakGame() {
        return this.game;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public boolean hasOwner() {
        return this.getOwner() != null;
    }

    public boolean isLeaked() {
        return this.leaked;
    }

    public abstract void leak(GoalHolder completer, GamePlayer player);

    public boolean registerGoal() {
        return true;
    }

    @Deprecated
    public abstract void resetLeakable();

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(GoalHolder owner) {
        this.owner = owner;
    }

    @Override
    public abstract String toString();

    private void handleGoalComplete(GoalHolder completer) {
        if (this.leaked) {
            return;
        }

        LeakableLeakEvent event = new LeakableLeakEvent(this.game.getPlugin(), this, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.leaked = true;
            this.leakedBy = event.getCompleter();

            // This game for this `GoalHolder` has been completed - we can tell
            // it to the plugin, so it can end the game. This method will loop
            // all `GameHolder`s (like players or teams) to find the winner.
            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
        }
    }
}
