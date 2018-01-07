package pl.themolka.arcade.capture;

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

public abstract class Capturable implements InteractableGoal, StringId {
    // The "ToStringStyle.SHORT_PREFIX_STYLE" strings are long and unreadable here.
    public static final ToStringStyle TO_STRING_STYLE = ToStringStyle.MULTI_LINE_STYLE;

    protected final CaptureGame game;

    private GoalHolder owner;
    protected boolean captured;
    protected GoalHolder capturedBy;
    private final GoalContributionContext contributions;
    private final String id;
    private String name;

    public Capturable(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Capturable(CaptureGame game, GoalHolder owner, String id) {
        this.game = game;

        this.owner = owner;
        this.captured = false;
        this.contributions = new GoalContributionContext();
        this.id = id;
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
        return Goal.isCompletableByNegative(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return this.isCaptured();
    }

    @Override
    public boolean isCompleted(GoalHolder completer) {
        return this.isCompleted() && (this.capturedBy == null || this.capturedBy.equals(completer));
    }

    @Override
    public boolean reset() {
        if (!this.isCaptured()) {
            return false;
        }

        CapturableResetEvent event = new CapturableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.captured = false;
            this.capturedBy = null;
            this.contributions.clearContributors();
            this.resetCapturable();
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

    public abstract void capture(GoalHolder completer, GamePlayer player);

    public GoalHolder getCapturedBy() {
        return this.capturedBy;
    }

    public CaptureGame getCaptureGame() {
        return this.game;
    }

    public abstract String getDefaultName();

    public boolean hasName() {
        return this.name != null;
    }

    public boolean hasOwner() {
        return this.getOwner() != null;
    }

    public boolean isCaptured() {
        return this.captured;
    }

    public boolean registerGoal() {
        return true;
    }

    @Deprecated
    public abstract void resetCapturable();

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(GoalHolder owner) {
        this.owner = owner;
    }

    @Override
    public abstract String toString();

    private void handleGoalComplete(GoalHolder completer) {
        if (this.captured) {
            return;
        }

        CapturableCaptureEvent event = new CapturableCaptureEvent(this.game.getPlugin(), this, completer);
        if (!event.isCanceled()) {
            this.captured = true;
            this.capturedBy = event.getCompleter();

            // This game for this `GoalHolder` has been completed - we can tell
            // it to the plugin, so it can end the game. This method will loop
            // all `GameHolder`s (like players or teams) to find the winner.
            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
        }
    }
}
