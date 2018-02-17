package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link MatchWinner} which specifies a draw win.
 */
public class DrawMatchWinner implements MatchWinner {
    public static final Color WINNER_COLOR = Color.YELLOW;
    public static final String WINNER_ID = "_draw-match-winner";

    public static final int GOALS_COUNT = 0;

    private final Match match;

    /**
     * Should ONLY be constructed from the {@link Match} class.
     */
    protected DrawMatchWinner(Match match) {
        this.match = match;
    }

    @Override
    public boolean addGoal(Goal goal) {
        return false;
    }

    @Override
    public boolean areGoalsCompleted() {
        return true;
    }

    @Override
    public boolean canParticipate() {
        return true;
    }

    @Override
    public boolean contains(Player bukkit) {
        return bukkit != null && this.contains(this.match.getGame().getPlugin().getPlayer(bukkit));
    }

    @Override
    public boolean contains(ArcadePlayer player) {
        GamePlayer gamePlayer = player.getGamePlayer();
        return gamePlayer != null && this.contains(gamePlayer);
    }

    @Override
    public boolean contains(GamePlayer player) {
        return player != null && player.isParticipating();
    }

    @Override
    public int countGoals() {
        return GOALS_COUNT;
    }

    @Override
    public Color getColor() {
        return WINNER_COLOR;
    }

    @Override
    public List<Goal> getGoals() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return WINNER_ID;
    }

    @Override
    public String getMessage() {
        return this.getTitle() + ChatColor.GREEN + "!";
    }

    @Override
    public String getName() {
        return "Score Draw";
    }

    @Override
    public Set<GamePlayer> getPlayers() {
        Set<GamePlayer> players = new HashSet<>();
        for (ArcadePlayer online : this.match.getGame().getPlugin().getPlayers()) {
            GamePlayer inGame = online.getGamePlayer();

            if (inGame != null && inGame.isParticipating()) {
                players.add(inGame);
            }
        }

        return players;
    }

    @Override
    public String getTitle() {
        return this.getColor().toChat() + ChatColor.BOLD.toString() + this.getName() + ChatColor.RESET;
    }

    @Override
    public boolean hasAnyGoals() {
        return true;
    }

    @Override
    public boolean hasGoal(Goal goal) {
        return true;
    }

    @Override
    public boolean isParticipating() {
        return true;
    }

    @Override
    public boolean removeGoal(Goal goal) {
        return false;
    }

    @Override
    public void sendGoalMessage(String message) {
        for (GamePlayer player : this.getPlayers()) {
            player.sendGoalMessage(message);
        }
    }

    public Match getMatch() {
        return this.match;
    }
}
