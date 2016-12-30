package pl.themolka.arcade.match;

import pl.themolka.arcade.game.GamePlayer;

public interface MatchWinner {
    String getName();

    boolean isWinning(GamePlayer player);
}
