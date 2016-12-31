package pl.themolka.arcade.team;

import pl.themolka.arcade.match.MatchWinner;

public class TeamMatchWinner implements MatchWinner {
    private final Team team;

    public TeamMatchWinner(Team team) {
        this.team = team;
    }

    @Override
    public String getName() {
        return this.getTeam().getPrettyName();
    }

    @Override
    public boolean isWinning() {
        return false;
    }

    public Team getTeam() {
        return this.team;
    }
}
