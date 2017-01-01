package pl.themolka.arcade.match;

import org.bukkit.ChatColor;

public interface MatchWinner {
    default String getMessage() {
        return this.getTitle() + ChatColor.GREEN + " won the game!";
    }

    String getName();

    String getTitle();

    boolean isWinning();
}
