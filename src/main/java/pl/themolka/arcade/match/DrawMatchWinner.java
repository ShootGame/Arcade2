package pl.themolka.arcade.match;

import org.bukkit.ChatColor;

public class DrawMatchWinner implements MatchWinner {
    @Override
    public String getMessage() {
        return this.getTitle() + "!";
    }

    @Override
    public String getName() {
        return "Score Draw";
    }

    @Override
    public String getTitle() {
        return ChatColor.GREEN + ChatColor.BOLD.toString() + this.getName() + ChatColor.RESET;
    }

    @Override
    public boolean isWinning() {
        return true;
    }
}
