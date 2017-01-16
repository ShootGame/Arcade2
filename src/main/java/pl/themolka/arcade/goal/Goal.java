package pl.themolka.arcade.goal;

import pl.themolka.arcade.match.MatchWinner;

public interface Goal {
    String getName();

    boolean isScorable(MatchWinner winner);

    boolean isScored(MatchWinner winner);

    void setScored(MatchWinner winner, boolean scored);
}
