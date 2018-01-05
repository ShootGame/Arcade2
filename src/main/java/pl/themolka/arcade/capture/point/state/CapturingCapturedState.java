package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;

public class CapturingCapturedState extends CapturingState {
    private final LosingState losingState;

    public CapturingCapturedState(LosingState losing, GoalHolder capturing) {
        this(losing.point, capturing);
    }

    public CapturingCapturedState(CapturedState captured, GoalHolder capturing) {
        this(captured.point, capturing);
    }

    public CapturingCapturedState(Point point, GoalHolder capturing) {
        super(point, capturing);

        this.losingState = new LosingState(this, capturing);
    }

    @Override
    public ChatColor getColor() {
        return this.getDominatingState().getColor();
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        // The point must be lost to be captured
        this.losingState.heartbeat(ticks, match, competitors, dominators, owner); // losing state
        super.heartbeat(ticks, match, competitors, dominators, owner); // capturing state
    }

    @Override
    public void setProgress(double progress) {
        super.setProgress(progress);
        this.losingState.setProgress(DONE - progress); // reverse
    }

    public PointState.Progress getDominatingState() {
        // progress < 50%  -> losing
        // progress => 50% -> capturing
        if (this.getProgress() < DONE / 2D) {
            return this.getLosingState();
        }

        return this;
    }

    public LosingState getLosingState() {
        return this.losingState;
    }
}
