package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;

import java.util.Collections;
import java.util.List;

/**
 * A {@link MatchWinner} which specifies a draw win.
 */
public class DrawMatchWinner implements MatchWinner {
    public static final String DRAW_WINNER_ID = "_draw-match-winner";

    private final ArcadePlugin plugin;

    /**
     * Should be constructed ONLY from the {@link Match} class.
     */
    protected DrawMatchWinner(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean addGoal(Goal goal) {
        return false;
    }

    @Override
    public boolean areGoalsCompleted() {
        return true;
    }

    @Override
    public boolean contains(Player bukkit) {
        return true;
    }

    @Override
    public boolean contains(ArcadePlayer player) {
        return this.contains(player.getGamePlayer());
    }

    @Override
    public boolean contains(GamePlayer player) {
        return player.isParticipating();
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

    @Override
    public List<Goal> getGoals() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return DRAW_WINNER_ID;
    }

    @Override
    public String getMessage() {
        return this.getTitle() + ChatColor.GREEN + "!";
    }

    @Override
    public String getName() {
        return "Score Draw";
    }

    @Override
    public String getTitle() {
        return this.getColor().toChat() + ChatColor.BOLD.toString() + this.getName() + ChatColor.RESET;
    }

    @Override
    public boolean hasGoal(Goal goal) {
        return true;
    }

    @Override
    public boolean removeGoal(Goal goal) {
        return false;
    }

    @Override
    public void sendGoalMessage(String message) {
    }
}
