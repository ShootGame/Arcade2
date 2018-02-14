package pl.themolka.arcade.match;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiMatchWinner implements MatchWinner {
    public static final Color WINNER_COLOR = Color.AQUA;

    private final String id;
    private final List<MatchWinner> winners;

    private MultiMatchWinner(String id, Collection<MatchWinner> winners) {
        this.id = id;
        this.winners = new ArrayList<>(winners);
    }

    @Override
    public boolean addGoal(Goal goal) {
        return false;
    }

    @Override
    public boolean canParticipate() {
        return true;
    }

    @Override
    public boolean contains(Player bukkit) {
        for (MatchWinner winner : this.winners) {
            if (winner.contains(bukkit)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int countGoals() {
        int count = 0;
        for (MatchWinner winner : this.winners) {
            count += winner.countGoals();
        }

        return count;
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
        return this.id;
    }

    @Override
    public Set<GamePlayer> getPlayers() {
        Set<GamePlayer> players = new HashSet<>();
        for (MatchWinner winner : this.winners) {
            players.addAll(winner.getPlayers());
        }

        return players;
    }

    @Override
    public String getTitle() {
        List<String> names = new ArrayList<>();
        for (MatchWinner winner : this.winners) {
            names.add(winner.getTitle());
        }

        return StringUtils.join(names, this.getColor().toChat() + ", " + ChatColor.RESET);
    }

    @Override
    public boolean hasAnyGoals() {
        for (MatchWinner winner : this.winners) {
            if (winner.hasAnyGoals()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasGoal(Goal goal) {
        for (MatchWinner winner : this.winners) {
            if (winner.hasGoal(goal)) {
                return true;
            }
        }

        return false;
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
        for (MatchWinner winner : this.winners) {
            winner.sendGoalMessage(message);
        }
    }

    public List<MatchWinner> getWinners() {
        return new ArrayList<>(this.winners);
    }

    public MatchWinner multiOrDraw(DrawMatchWinner draw, Collection<MatchWinner> winners) {
        if (this.winners.size() != winners.size()) {
            return this;
        }

        for (MatchWinner winner : winners) {
            if (!this.winners.contains(winner)) {
                return this;
            }
        }

        return draw;
    }

    //
    // Instancing
    //

    public static MultiMatchWinner of(Collection<MatchWinner> winners) {
        if (winners != null) {
            winners = unpack(winners);

            if (!winners.isEmpty()) {
                return new MultiMatchWinner(buildUniqueId(winners), winners);
            }
        }

        throw new IllegalArgumentException("Illegal winner collection.");
    }

    public static MultiMatchWinner of(MatchWinner... winners) {
        return of(Arrays.asList(winners));
    }

    private static String buildUniqueId(Collection<MatchWinner> winners) {
        List<String> ids = new ArrayList<>();
        for (MatchWinner winner : winners) {
            ids.add(winner.getId());
        }

        return "_multi-match-winner:" + StringUtils.join(ids, "|");
    }

    private static List<MatchWinner> unpack(Collection<MatchWinner> winners) {
        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : winners) {
            if (winner instanceof MultiMatchWinner) {
                results.addAll(((MultiMatchWinner) winner).getWinners());
            } else {
                results.add(winner);
            }
        }

        return results;
    }
}
