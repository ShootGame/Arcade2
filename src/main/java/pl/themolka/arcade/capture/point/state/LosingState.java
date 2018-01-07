package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.capture.point.PointLostEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public class LosingState extends PointState.Progress {
    public static final double LOST = Progress.ZERO;

    private final GoalHolder loser; // not null

    public LosingState(CapturedState captured, GoalHolder loser) {
        this(captured.point, loser);
    }

    public LosingState(CapturingState capturing) {
        this(capturing.point, capturing.getCapturer());
    }

    public LosingState(Point point, GoalHolder loser) {
        super(point);

        this.loser = loser;
    }

    @Override
    public PointState copy() {
        return new LosingState(this.point, this.loser);
    }

    @Override
    public ChatColor getColor() {
        return this.loser.getColor().toChat();
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        boolean ownerDominating = false;

        if (owner != null) {
            for (GoalHolder competitor : dominators.keySet()) {
                if (owner.equals(competitor)) {
                    // current owner
                    ownerDominating = true;
                    break;
                }
            }
        }

        if (ownerDominating) {
            // The owner is dominating the point - bring it back to him.
            this.startCapturing(this.point, this.loser, this.getProgress());
            return;
        }

        // Progress the state if there are any dominators on the point.
        if (!dominators.isEmpty()) {
            this.progress();
        }

        if (this.getProgress() <= LOST) { // The point is lost at 0%.
            NeutralState neutralState = this.point.createNeutralState();

            PointLostEvent event = new PointLostEvent(this.game.getPlugin(), this.point, this, neutralState, this.loser);
            this.game.getPlugin().getEventBus().publish(event);

            if (event.isCanceled()) {
                return;
            }

            PointState newState = event.getNewState();
            if (newState instanceof NeutralState && owner != null) {
                this.point.lose(owner);
            }

            this.point.setState(newState);
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

    public PointState startCapturing(Point point, GoalHolder capturer, double progress) {
        return point.startCapturing(capturer, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .append("progress", this.getProgress())
                .append("loser", this.loser)
                .build();
    }
}
