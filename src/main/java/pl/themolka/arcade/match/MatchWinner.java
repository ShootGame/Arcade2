package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.session.ArcadePlayer;

public interface MatchWinner extends GoalHolder {
    boolean contains(Player bukkit);

    default boolean contains(ArcadePlayer player) {
        return this.contains(player.getBukkit());
    }

    default boolean contains(GamePlayer player) {
        return this.contains(player.getPlayer());
    }

    default String getMessage() {
        return this.getTitle() + ChatColor.GREEN + " won the game!";
    }

    default String getTitle() {
        return this.getColor().toChat() + this.getName() + ChatColor.RESET;
    }
}
