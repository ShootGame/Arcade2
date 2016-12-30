package pl.themolka.arcade.match;

import com.google.common.eventbus.Subscribe;
import org.bukkit.ChatColor;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.session.Session;

import java.util.ArrayList;
import java.util.List;

public class MatchGame extends GameModule {
    public static final String METADATA_MATCH = "match";

    private int defaultStartCountdown;
    private Match match;
    private MatchStartCountdown startCountdown;
    private final List<MatchWinner> winnerList = new ArrayList<>();

    public MatchGame(int defaultStartCountdown) {
        this.defaultStartCountdown = defaultStartCountdown;
    }

    @Override
    public void onEnable() {
        this.match = new Match(this.getPlugin(), this.getGame());

        this.getGame().setMetadata(MatchModule.class, METADATA_MATCH, this.getMatch());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public MatchWinner findWinner() {
        return this.findWinner(null);
    }

    public MatchWinner findWinner(String query) {
        if (query != null) {
            for (MatchWinner winner : this.getWinnerList()) {
                if (winner.getName().equalsIgnoreCase(query)) {
                    return winner;
                }
            }

            for (MatchWinner winner : this.getWinnerList()) {
                if (winner.getName().toLowerCase().contains(query.toLowerCase())) {
                    return winner;
                }
            }
        } else {
            for (MatchWinner winner : this.getWinnerList()) {
                if (winner.isWinning()) {
                    return winner;
                }
            }
        }

        return null;
    }

    public int getDefaultStartCountdown() {
        return this.defaultStartCountdown;
    }

    public Match getMatch() {
        return this.match;
    }

    public MatchStartCountdown getStartCountdown() {
        return this.startCountdown;
    }

    public List<MatchWinner> getWinnerList() {
        return this.winnerList;
    }

    public void handleBeginCommand(Session<ArcadePlayer> sender, int seconds, boolean force) {
        String message = "Starting";
        if (force) {
            message = "Force starting";
        }

        sender.sendSuccess(message + " the match in " + seconds + " seconds...");
        this.startCountdown(seconds);
    }

    public void handleEndCommand(Session<ArcadePlayer> sender, boolean auto, String winnerQuery, boolean draw) {
        MatchWinner winner = null;
        if (auto) {
            winner = this.findWinner();
        } else if (draw) {
            winner = new DrawWinner();
        } else if (winnerQuery != null) {
            winner = this.findWinner(winnerQuery);
        }

        sender.sendSuccess("Force ending the match...");
        this.getMatch().end(winner, true);
    }

    public List<String> handleEndCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        List<String> results = new ArrayList<>();
        for (MatchWinner winner : this.getWinnerList()) {
            results.add(winner.getName());
        }

        return results;
    }

    @Subscribe
    public void onJoinWhenMatchEnded(GameCommands.JoinCommandEvent event) {
        if (this.getMatch().getState().equals(MatchState.CYCLING)) {
            event.getSender().sendError("The match has ended. " + ChatColor.GOLD + "Please wait until the server cycle.");
            event.setCanceled(true);
        }
    }

    @Subscribe
    public void onMatchCountdownAutoStart(GameCommands.JoinCommandEvent event) {
        if (!this.getMatch().cannotStart() && !this.getStartCountdown().isTaskRunning()) {
            this.startCountdown(this.getDefaultStartCountdown());
        }
    }

    @Subscribe
    public void onMatchTimeDescribe(GameCommands.GameCommandEvent event) {
        String time;
        if (this.getMatch().getStartTime() != null) {
            time = this.getMatch().getStartTime().toString();
        } else {
            time = this.getMatch().getTime().toString();
        }

        event.getSender().send(ChatColor.DARK_PURPLE + "Time: " + ChatColor.DARK_AQUA + time);
    }

    public boolean registerWinner(MatchWinner winner) {
        return this.winnerList.add(winner);
    }

    public int startCountdown(int seconds) {
        if (this.getStartCountdown() == null) {
            this.startCountdown = new MatchStartCountdown(this.getPlugin(), this.match);
            this.getStartCountdown().setGame(this.getGame());
        }

        return this.getStartCountdown().countStart(seconds);
    }

    public boolean unregisterWinner(MatchWinner winner) {
        return this.winnerList.remove(winner);
    }
}
