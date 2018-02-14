package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.Participator;

public interface MatchWinner extends Participator {
    default String getMessage() {
        return this.getTitle() + ChatColor.GREEN + " won the game!";
    }
}
