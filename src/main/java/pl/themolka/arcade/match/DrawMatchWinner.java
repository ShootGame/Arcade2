package pl.themolka.arcade.match;

public class DrawMatchWinner implements MatchWinner {
    @Override
    public String getMessage() {
        return this.getName() + "!";
    }

    @Override
    public String getName() {
        return "Score Draw";
    }

    @Override
    public boolean isWinning() {
        return true;
    }
}
