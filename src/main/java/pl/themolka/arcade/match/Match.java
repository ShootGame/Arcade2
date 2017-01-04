package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Match {
    private final ArcadePlugin plugin;

    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private IObserverHandler observerHandler;
    private final Observers observers;
    private final ObserversKit observersKit;
    private Instant startTime;
    private MatchState state = MatchState.STARTING;
    private Duration time;
    private final List<MatchWinner> winnerList = new ArrayList<>();

    public Match(ArcadePlugin plugin, Game game, Observers observers) {
        this.plugin = plugin;

        this.game = game;
        this.observers = observers;
        this.observersKit = new ObserversKit(plugin);
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
            player.send(" " + Commands.createLine(Commands.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(Commands.createTitle(ChatColor.GOLD + "The match has ended!"));
            if (winnerMessage != null) {
                player.send(winnerMessage);
            }
            player.send(" " + Commands.createLine(Commands.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
        }
    }

    public void broadcastStartMessage() {
        for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            ArcadePlayer player = gamePlayer.getPlayer();
            player.send(" " + Commands.createLine(Commands.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(Commands.createTitle(ChatColor.GREEN + "The match has started!"));
            if (gamePlayer.isParticipating()) {
                player.send(Commands.createTitle(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Good luck!"));
            }
            player.send(" " + Commands.createLine(Commands.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
        }
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
        this.plugin.getEventBus().publish(endEvent);

        if (endEvent.isCanceled()) {
            return;
        }

        this.time = Duration.between(this.startTime, Instant.now());

        this.broadcastEndMessage(winner);
        this.setForceEnd(force);
        this.setState(MatchState.CYCLING);

        MatchEndedEvent endedEvent = new MatchEndedEvent(this.plugin, this, winner, force);
        this.plugin.getEventBus().publish(endedEvent);

        for (GamePlayer player : this.getGame().getPlayers()) {
            if (player.isOnline()) {
                player.reset();
            }
        }
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
    
    public Game getGame() {
        return this.game;
    }

    public IObserverHandler getObserverHandler() {
        return this.observerHandler;
    }

    public Observers getObservers() {
        return this.observers;
    }

    public ObserversKit getObserversKit() {
        return this.observersKit;
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

    public List<MatchWinner> getWinnerList() {
        return this.winnerList;
    }

    public boolean isForceEnd() {
        return this.forceEnd;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }

    public boolean isObserving(GamePlayer player) {
        IObserverHandler handler = this.getObserverHandler();
        boolean observing = this.getObservers().hasPlayer(player) || handler == null || handler.isPlayerObserving(player);

        return !this.getState().equals(MatchState.RUNNING) && observing;
    }

    public boolean registerWinner(MatchWinner winner) {
        return this.winnerList.add(winner);
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
        this.plugin.getEventBus().publish(startEvent);

        if (startEvent.isCanceled()) {
            return;
        }

        this.startTime = Instant.now();

        this.broadcastStartMessage();

        this.setForceStart(force);
        this.setState(MatchState.RUNNING);

        MatchStartedEvent startedEvent = new MatchStartedEvent(this.plugin, this, force);
        this.plugin.getEventBus().publish(startedEvent);
    }

    public boolean unregisterWinner(MatchWinner winner) {
        return this.winnerList.remove(winner);
    }
    
    public interface IObserverHandler {
        boolean isPlayerObserving(GamePlayer player);
    }
}
