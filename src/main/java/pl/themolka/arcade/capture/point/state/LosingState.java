package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.capture.point.PointLostEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public class LosingState extends PointState.Progress {
    private final GoalHolder loser;

    public LosingState(CapturedState captured, GoalHolder loser) {
        this(captured.point, loser);
    }

    public LosingState(CapturingState capturing, GoalHolder loser) {
        this(capturing.point, loser);
    }

    public LosingState(Point point, GoalHolder loser) {
        super(point);

        this.loser = loser;
    }

    @Override
    public ChatColor getColor() {
        GoalHolder owner = this.getOwner();
        if (owner != null) {
            return owner.getColor().toChat();
        }

        // this should never happen
        return NeutralState.NEUTRAL_COLOR;
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        boolean ownerDominating = false;

        for (GoalHolder competitor : dominators.keySet()) {
            if (owner.equals(competitor)) {
                // current owner
                ownerDominating = true;
                break;
            }
        }

        if (ownerDominating) {
            // The owner is dominating the point - bring it back to him.
            if (this.point.isCapturingCapturedEnabled()) {
                this.point.startCapturingCaptured(owner, this.getProgress());
            } else {
                this.point.startCapturing(owner, this.getProgress());
            }
            return;
        }

        // Progress the state if there are any dominators on the point.
        if (!dominators.isEmpty()) {
            this.progress();
        }

        if (this.getProgress() <= ZERO) { // the point is lost at 0%
            NeutralState neutralState = this.point.createNeutralState(this);

            PointLostEvent event = new PointLostEvent(this.game.getPlugin(), this.point, this, neutralState, this.loser);
            this.game.getPlugin().getEventBus().publish(event);

            if (event.isCanceled()) {
                return;
            }

            PointState newState = event.getNewState();
            this.point.setState(newState);

            if (newState instanceof NeutralState) {
                this.point.lose(event.getLoser());
            }
        }
    }

    @Override
    public Time getProgressTime() {
        return this.point.getLoseTime();
    }

    @Override
    public boolean isProgressPositive() {
        return false;
    }

    public GoalHolder getLoser() {
        return this.loser;
    }

    public GoalHolder getOwner() {
        return this.point.getOwner();
    }
}
