package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.List;

public interface MatchWinner {
    boolean addGoal(Goal goal);

    boolean contains(Player bukkit);

    default boolean contains(ArcadePlayer player) {
        return this.contains(player.getBukkit());
    }

    default boolean contains(GamePlayer player) {
        return this.contains(player.getPlayer());
    }

    List<Goal> getGoals();

    default String getMessage() {
        return this.getTitle() + ChatColor.GREEN + " won the game!";
    }

    String getName();

    String getTitle();

    default boolean hasGoal(Goal goal) {
        return this.getGoals().contains(goal);
    }

    boolean isWinning();

    boolean removeGoal(Goal goal);
}
