package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.session.ArcadePlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                winnerMessage = CommandUtils.createTitle(ChatColor.GOLD + winner.getMessage());
            }

            ArcadePlayer player = gamePlayer.getPlayer();
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(CommandUtils.createTitle(ChatColor.GOLD + "The match has ended!"));
            if (winnerMessage != null) {
                player.send(winnerMessage);
            }
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
        }
    }

    public void broadcastStartMessage() {
        for (GamePlayer gamePlayer : this.getGame().getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            ArcadePlayer player = gamePlayer.getPlayer();
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
            player.send(CommandUtils.createTitle(ChatColor.GREEN + "The match has started!"));
            if (gamePlayer.isParticipating()) {
                player.send(CommandUtils.createTitle(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Good luck!"));
            }
            player.send(" " + CommandUtils.createLine(CommandUtils.CHAT_LINE_LENGTH) + ChatColor.RESET + " ");
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

        ItemStack compass = new ItemStackBuilder().type(Material.COMPASS).build();
        for (GamePlayer player : this.getGame().getPlayers()) {
            if (player.isOnline()) {
                player.getPlayer().reset();
                player.getBukkit().getInventory().addItem(compass);
            }
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
    public List<MatchWinner> getWinnerList() {
        return this.winnerList;
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

    public void refreshWinners() {
        MatchWinner winner = this.getWinner();
        if (winner != null) {
            this.end(winner);
        }
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
