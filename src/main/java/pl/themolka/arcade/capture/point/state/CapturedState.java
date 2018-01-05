package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;

import java.util.ArrayList;
import java.util.List;

public class CapturedState extends PointState.Permanent {
    public CapturedState(CapturingState capturing) {
        this(capturing.point);
    }

    public CapturedState(Point point) {
        super(point);
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
    public double getProgress() {
        return Goal.PROGRESS_SCORED;
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        List<GoalHolder> enemies = new ArrayList<>();
        boolean ownerDominating = false;

        for (GoalHolder competitor : dominators.keySet()) {
            if (owner.equals(competitor)) {
                // current owner
                enemies.clear();
                ownerDominating = true;
                break;
            }

            enemies.add(competitor);
        }

        if (!ownerDominating) {
            // The owner is not dominating the point, begin losing it, or
            // start capturing if the capturing captured mode is enabled.
            // If there are more than one enemies on the point and
            // capturing captured mode is enabled - start losing it.

            GoalHolder enemy = null;
            if (enemies.size() == 1) {
                enemy = enemies.get(0);
            }

            if (this.point.isCapturingCaptured() && enemy != null) {
                this.point.startCapturingCaptured(enemy, Progress.ZERO);
            } else {
                this.point.startLosing(owner, Progress.DONE);
            }
        }
    }

    public GoalHolder getOwner() {
        return this.point.getOwner();
    }
}
