package pl.themolka.arcade.capture.flag;

import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.NeutralState;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;

public class Flag extends Capturable {
    public static final String DEFAULT_GOAL_NAME = "Flag";

    private boolean flagTouched = false;
    private final FlagState initialState;
    private FlagState state;

    public Flag(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Flag(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        this.initialState = new NeutralState(this);

        // Set the current state to a copy the initial state.
        this.state = this.getInitialState().copy();
    }

    @Override
    public void capture(GoalHolder completer, GamePlayer player) {
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        return null;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return super.isCompletableBy(completer);
    }

    @Override
    public boolean isUntouched() {
        return !this.flagTouched;
    }

    @Override
    public void resetCapturable() {
        this.flagTouched = false;
        this.state = this.getInitialState().copy();
    }

    public FlagState getInitialState() {
        return this.initialState;
    }

    public FlagState getState() {
        return this.state;
    }

    public void setState(FlagState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("captured", this.isCaptured())
                .append("capturedBy", this.getCapturedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("flagTouched", this.flagTouched)
                .build();
    }
}
