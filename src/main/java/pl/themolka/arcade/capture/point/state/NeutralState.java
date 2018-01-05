package pl.themolka.arcade.capture.point.state;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;

import java.util.ArrayList;

public class NeutralState extends PointState.Permanent {
    public static final ChatColor NEUTRAL_COLOR = ChatColor.WHITE;

    public NeutralState(LosingState losing) {
        this(losing.point);
    }

    public NeutralState(Point point) {
        super(point);
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
        // The neutral state can only be captured.
        if (dominators.size() == 1) {
            GoalHolder dominator = new ArrayList<>(dominators.keySet()).get(0);

            if (dominator != null) {
                this.point.startCapturing(dominator, Progress.ZERO);
            }
        }
    }
}
