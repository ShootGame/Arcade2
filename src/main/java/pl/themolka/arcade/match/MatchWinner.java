package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.goal.GoalHolder;

public interface MatchWinner extends GoalHolder {
    default String getMessage() {
        return this.getTitle() + ChatColor.GREEN + " won the game!";
    }
}
