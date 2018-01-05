package pl.themolka.arcade.goal;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public interface GoalHolder extends StringId {
    boolean addGoal(Goal goal);

    default boolean areGoalsCompleted() {
        for (Goal goal : this.getGoals()) {
            if (!goal.isCompleted(this)) {
                return false;
            }
        }

        return true;
    }

    boolean contains(Player bukkit);

    default boolean contains(ArcadePlayer player) {
        GamePlayer gamePlayer = player.getGamePlayer(); // should never be null
        return gamePlayer != null && this.contains(gamePlayer);
    }

    default boolean contains(GamePlayer player) {
        Player bukkit = player.getBukkit(); // null if the player if offline
        return bukkit != null && this.contains(bukkit);
    }

    Color getColor();

    List<Goal> getGoals();

    default String getName() {
        return this.getId();
    }

    default String getTitle() {
        return this.getColor().toChat() + this.getName() + ChatColor.RESET;
    }

    boolean hasGoal(Goal goal);

    boolean removeGoal(Goal goal);

    void sendGoalMessage(String message);
}
