package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;

import java.util.ArrayList;

public class NeutralState extends PointState.Permanent {
    public static final ChatColor NEUTRAL_COLOR = ChatColor.GOLD; // WHITE is awful

    public NeutralState(LosingState losing) {
        this(losing.point);
    }

    public NeutralState(Point point) {
        super(point);
    }

    @Override
    public PointState copy() {
        return new NeutralState(this.point);
    }

    @Override
    public ChatColor getColor() {
        return NEUTRAL_COLOR;
    }

    @Override
    public double getProgress() {
        return Goal.PROGRESS_UNTOUCHED;
    }

    @Override
    public void heartbeat(long ticks, Match match, Multimap<GoalHolder, GamePlayer> competitors,
                          Multimap<GoalHolder, GamePlayer> dominators, GoalHolder owner) {
        // The neutral state can only be captured by one dominator.
        if (dominators.size() == 1) {
            GoalHolder dominator = new ArrayList<>(dominators.keySet()).get(0);

            if (dominator != null) {
                this.startCapturing(this.point, dominator, Progress.ZERO);
            }
        }
    }

    public PointState startCapturing(Point point, GoalHolder capturer, double progress) {
        return point.startCapturing(capturer, progress);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("point", this.point)
                .build();
    }
}
