package pl.themolka.arcade.match;

import pl.themolka.arcade.game.GamePlayer;

public class DrawWinner implements MatchWinner {
    @Override
    public String getName() {
        return "Score draw!";
    }

    @Override
    public boolean isWinning(GamePlayer player) {
        return true;
    }
}
