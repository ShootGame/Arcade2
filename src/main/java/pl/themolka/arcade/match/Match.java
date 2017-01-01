package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.time.Duration;
import java.time.Instant;

public class Match {
    private final ArcadePlugin plugin;

    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private IObserverHandler observerHandler;
    private Instant startTime;
    private MatchState state = MatchState.STARTING;
    private Duration time;

    public Match(ArcadePlugin plugin, Game game) {
        this.plugin = plugin;

        this.game = game;
    }

    public void broadcastEndMessage(MatchWinner winner) {
        for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            String winnerMessage = null;
            if (winner != null) {
                winnerMessage = Commands.createTitle(ChatColor.GOLD + winner.getMessage());
            }

            ArcadePlayer player = gamePlayer.getPlayer();
            player.send(Commands.createLine(Commands.CHAT_LINE_LENGTH));
            player.send(Commands.createTitle(ChatColor.GOLD + "The match has ended!"));
            if (winnerMessage != null) {
                player.send(winnerMessage);
            }
            player.send(Commands.createLine(Commands.CHAT_LINE_LENGTH));
        }
    }

    public void broadcastStartMessage() {
        for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            ArcadePlayer player = gamePlayer.getPlayer();
            player.send(Commands.createLine(Commands.CHAT_LINE_LENGTH));
            player.send(Commands.createTitle(ChatColor.GREEN + "The match has started!"));
            player.send(Commands.createLine(Commands.CHAT_LINE_LENGTH));
        }
    }

    public boolean cannotStart() {
        return false;
    }

    public void end(boolean force) {
        this.end(null, force);
    }

    public void end(MatchWinner winner) {
        this.end(winner, false);
    }

    public void end(MatchWinner winner, boolean force) {
        if (!this.getState().equals(MatchState.RUNNING)) {
            return;
        }

        MatchEndEvent endEvent = new MatchEndEvent(this.plugin, this, winner, force);
        this.plugin.getEvents().post(endEvent);

        if (endEvent.isCanceled()) {
            return;
        }

        this.time = Duration.between(this.startTime, Instant.now());

        this.broadcastEndMessage(winner);

        this.setForceEnd(force);
        this.setState(MatchState.CYCLING);
        for (GamePlayer player : this.getGame().getPlayers()) {
            player.reset();
        }
    }
    
    public Game getGame() {
        return this.game;
    }

    public IObserverHandler getObserverHandler() {
        return this.observerHandler;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public MatchState getState() {
        return this.state;
    }

    public Duration getTime() {
        return this.time;
    }

    public boolean isForceEnd() {
        return this.forceEnd;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }

    public boolean isPlayerObserving(GamePlayer player) {
        IObserverHandler handler = this.getObserverHandler();
        return handler == null || handler.isPlayerObserving(player);
    }

    public void setForceEnd(boolean forceEnd) {
        this.forceEnd = forceEnd;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public void setObserverHandler(IObserverHandler observerHandler) {
        this.observerHandler = observerHandler;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public void start(boolean force) {
        if (!this.getState().equals(MatchState.STARTING)) {
            return;
        }

        MatchStartEvent startEvent = new MatchStartEvent(this.plugin, this, force);
        this.plugin.getEvents().post(startEvent);

        if (startEvent.isCanceled()) {
            return;
        }

        this.startTime = Instant.now();

        this.broadcastStartMessage();

        this.setForceStart(force);
        this.setState(MatchState.RUNNING);
    }

    public interface IObserverHandler {
        boolean isPlayerObserving(GamePlayer player);
    }
}
