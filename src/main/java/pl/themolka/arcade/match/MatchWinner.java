package pl.themolka.arcade.match;

public interface MatchWinner {
    default String getMessage() {
        return this.getName() + " wins!";
    }

    String getName();

    boolean isWinning();
}
