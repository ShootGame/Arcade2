package pl.themolka.arcade.match;

import com.google.common.eventbus.Subscribe;
import org.bukkit.ChatColor;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.session.Session;

public class MatchGame extends GameModule {
    public static final String METADATA_MATCH = "match";

    private int defaultStartCountdown;
    private Match match;
    private MatchStartCountdown startCountdown;

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

    public int getDefaultStartCountdown() {
        return this.defaultStartCountdown;
    }

    public Match getMatch() {
        return this.match;
    }

    public MatchStartCountdown getStartCountdown() {
        return this.startCountdown;
    }

    public void handleBeginCommand(Session<ArcadePlayer> sender, int seconds, boolean force) {
        String message = "Starting";
        if (force) {
            message = "Force starting";
        }

        sender.sendSuccess(message + " the match in " + seconds + " seconds...");
        this.startCountdown(seconds);
    }

    public void handleEndCommand(Session<ArcadePlayer> sender, String winnerQuery, boolean draw) {
        MatchWinner winner = null;
        if (draw) {
            winner = new DrawWinner();
        } else {

        }

        sender.sendSuccess("Force ending the match...");
        this.getMatch().end(winner, true);
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

    public int startCountdown(int seconds) {
        if (this.getStartCountdown() == null) {
            this.startCountdown = new MatchStartCountdown(this.getPlugin(), this.match);
            this.getStartCountdown().setGame(this.getGame());
        }

        return this.getStartCountdown().countStart(seconds);
    }
}
