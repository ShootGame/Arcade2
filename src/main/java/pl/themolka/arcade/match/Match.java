package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private final ArcadePlugin plugin;

    private final DrawMatchWinner drawWinner = new DrawMatchWinner();
    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private IObserverHandler observerHandler;
    private final Observers observers;
    private final ObserversKit observersKit;
    private Instant startTime;
    private MatchState state = MatchState.STARTING;
    private Duration time;
    private final Map<String, MatchWinner> winnerMap = new HashMap<>();

    public Match(ArcadePlugin plugin, Game game, Observers observers) {
        this.plugin = plugin;

        this.game = game;
        this.observers = observers;
        this.observersKit = new ObserversKit(plugin);
    }

    public void broadcastEndMessage(MatchWinner winner) {
        String winnerMessage = null;
        if (winner != null) {
            winnerMessage = CommandUtils.createTitle(ChatColor.GOLD + winner.getMessage());
        }

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(" " + CommandUtils.createTitle(ChatColor.GOLD + "The match has ended!") + " ");
            if (winnerMessage != null) {
                player.send(" " + winnerMessage + " ");
            }
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
        }

        String logMessage = "no result.";
        if (winnerMessage != null) {
            logMessage = "result: " + winner.getMessage();
        }

        this.plugin.getLogger().info(ChatColor.stripColor("The match has ended with " + logMessage));
    }

    public void broadcastStartMessage() {
        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(" " + CommandUtils.createTitle(ChatColor.GREEN + "The match has started!") + " ");
            if (player.getGamePlayer() != null && player.getGamePlayer().isParticipating()) {
                player.send(" " + CommandUtils.createTitle(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Good luck!") + " ");
            }
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
        }

        this.plugin.getLogger().info("The match has started.");
    }

    public void end(boolean force) {
        this.end(null, force);
    }

    public void end(MatchWinner winner) {
        this.end(winner, false);
    }

    public void end(MatchWinner winner, boolean force) {
        if (!this.isRunning()) {
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

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            if (player.getGamePlayer() != null) {
                player.getGamePlayer().setParticipating(false);
            }

            player.getPermissions().clearGroups();
            player.getPermissions().refresh();

            player.refresh();

            player.getBukkit().getInventory().setItem(0, ObserversKit.TELEPORTER);
            player.getBukkit().setGameMode(ObserversKit.GAME_MODE);
        }
    }

    public MatchWinner findWinner(String query) {
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

        return null;
    }

    public MatchWinner findWinnerById(String id) {
        return this.winnerMap.get(id);
    }

    public MatchWinner findWinnerByPlayer(Player bukkit) {
        return this.findWinnerByPlayer(this.plugin.getPlayer(bukkit));
    }

    public MatchWinner findWinnerByPlayer(ArcadePlayer player) {
        return this.findWinnerByPlayer(player.getGamePlayer());
    }

    public MatchWinner findWinnerByPlayer(GamePlayer player) {
        for (MatchWinner winner : this.getWinnerList()) {
            if (winner.contains(player)) {
                return winner;
            }
        }

        return null;
    }

    public DrawMatchWinner getDrawWinner() {
        return this.drawWinner;
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

    /**
     * Returns currently winning `MatchWinner`, or `DrawWinner` (if draw), or `null`.
     */
    public MatchWinner getWinner() {
        List<MatchWinner> winners = this.getWinners();
        if (winners.isEmpty()) {
            return null;
        } else if (winners.size() == 1) {
            return winners.get(0);
        } else {
            return this.getDrawWinner();
        }
    }

    /**
     * Returns a `List` of all `MatchWinner` objects in this `Match`.
     */
    public Collection<MatchWinner> getWinnerList() {
        return this.winnerMap.values();
    }

    /**
     * Returns a `List` of currently winning `MatchWinner` objects in this `Match`.
     */
    public List<MatchWinner> getWinners() {
        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getWinnerList()) {
            if (winner.isWinning()) {
                results.add(winner);
            }
        }

        return results;
    }

    public boolean isCycling() {
        return this.getState().equals(MatchState.CYCLING);
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

        return !this.isRunning() || observing || !player.isParticipating();
    }

    public boolean isRunning() {
        return this.getState().equals(MatchState.RUNNING);
    }

    public boolean isStarting() {
        return this.getState().equals(MatchState.STARTING);
    }

    public void refreshWinners() {
        MatchWinner winner = this.getWinner();
        if (winner != null) {
            this.end(winner);
        }
    }

    public void registerWinner(MatchWinner winner) {
        this.winnerMap.put(winner.getId(), winner);
    }

    public void sendGoalMessage(String message) {
        this.plugin.getLogger().info(message);

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.send(message);
            player.sendAction(message);
        }
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
        if (!this.isStarting()) {
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

        for (ArcadePlayer online : this.plugin.getPlayers()) {
            online.getPermissions().clearGroups();
            online.getPermissions().refresh();

            GamePlayer player = online.getGamePlayer();
            if (player != null && player.isParticipating()) {
                player.refresh();
                player.getBukkit().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    public void unregisterWinner(MatchWinner winner) {
        this.winnerMap.remove(winner.getId());
    }

    public interface IObserverHandler {
        boolean isPlayerObserving(GamePlayer player);
    }
}
