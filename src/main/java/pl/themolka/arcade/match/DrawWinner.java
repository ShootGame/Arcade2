package pl.themolka.arcade.match;

public class DrawWinner implements MatchWinner {
    @Override
    public String getName() {
        return "Score draw!";
    }

    @Override
    public boolean isWinning() {
        return true;
    }
}
