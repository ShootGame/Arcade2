package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match implements DynamicWinnable {
    private final ArcadePlugin plugin;

    private final DrawMatchWinner drawWinner;
    private List<DynamicWinnable> dynamicWinnables = new ArrayList<>();
    private Time endTime = Time.ZERO;
    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private IObserverHandler observerHandler;
    private final Observers observers;
    private final ObserversKit observersKit;
    private PlayMatchWindow playWindow;
    private Time startTime = Time.ZERO;
    private MatchState state = MatchState.STARTING;
    private final Map<String, MatchWinner> winnerMap = new HashMap<>();

    public Match(ArcadePlugin plugin, Game game, Observers observers) {
        this.plugin = plugin;

        this.drawWinner = new DrawMatchWinner(this);
        this.game = game;
        this.observers = observers;
        this.observersKit = new ObserversKit(plugin);
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        List<MatchWinner> results = new ArrayList<>();
        for (DynamicWinnable winnable : this.dynamicWinnables) {
            MatchWinner winner = winnable.getDynamicWinner();

            if (winner != null) {
                results.add(winner);
            }
        }

        return results;
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

        this.endTime = Time.now();

        this.broadcastEndMessage(winner);
        this.setForceEnd(force);
        this.setState(MatchState.CYCLING);

        for (ArcadePlayer online : this.plugin.getPlayers()) {
            GamePlayer player = online.getGamePlayer();
            if (player == null || !player.isParticipating()) {
                continue;
            }

            player.setCurrentChannel(null);
            player.setParticipating(false);

            online.getPermissions().clearGroups();
            online.getPermissions().refresh();
            player.reset();

            player.getBukkit().getInventory().setItem(0, ObserversKit.NAVIGATION);
            player.getBukkit().getInventory().setHeldItemSlot(0);
            player.getBukkit().setGameMode(ObserversKit.GAME_MODE);
            player.getBukkit().setAllowFlight(true);
        }

        this.plugin.getEventBus().publish(new MatchEndedEvent(this.plugin, this, winner, force));
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

    public Time getEndTime() {
        return this.endTime;
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

    public PlayMatchWindow getPlayWindow() {
        return this.playWindow;
    }

    public Time getStartTime() {
        return this.startTime;
    }

    public MatchState getState() {
        return this.state;
    }

    public Time getTime() {
        return this.getTime(Time.now());
    }

    public Time getTime(Time now) {
        if (this.isStarting()) {
            return Time.ZERO;
        } else if (this.isRunning()) {
            return now.minus(this.getStartTime());
        } else if (this.isCycling()) {
            return this.getEndTime().minus(this.getStartTime());
        }

        return Time.ZERO;
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
            return MultiMatchWinner.of(winners);
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
            if (winner.areGoalsCompleted()) {
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
        if (player == null) {
            // This should never happen, but if it did - the players is
            // observing the game.
            return true;
        }

        IObserverHandler handler = this.getObserverHandler();
        boolean observing = this.getObservers().hasPlayer(player) ||
                (handler != null && handler.isPlayerObserving(player));

        return !this.isRunning() || observing || !player.isParticipating();
    }

    public boolean isRunning() {
        return this.getState().equals(MatchState.RUNNING);
    }

    public boolean isStarting() {
        return this.getState().equals(MatchState.STARTING);
    }

    public void matchEmpty(MatchWinner participant) {
        if (this.isRunning()) {
            MatchEmptyEvent event = new MatchEmptyEvent(this.plugin, this, participant);
            this.plugin.getEventBus().publish(event);

            if (!event.isCanceled()) {
                this.sendGoalMessage(ChatColor.RED + ChatColor.ITALIC.toString() +
                        "The match has ended due to the lack of players.");
                this.end(null);
            }
        }
    }

    public void refreshWinners() {
        if (this.isRunning()) {
            MatchWinner winner = this.getWinner();
            if (winner != null) {
                if (winner instanceof MultiMatchWinner) {
                    winner = ((MultiMatchWinner) winner).multiOrDraw(this.getDrawWinner(), this.getWinnerList());
                }

                this.end(winner);
            }
        }
    }

    public boolean registerDynamicWinnable(DynamicWinnable dynamicWinnable) {
        return this.dynamicWinnables.add(dynamicWinnable);
    }

    public void registerWinner(MatchWinner winner) {
        this.winnerMap.put(winner.getId(), winner);
    }

    public void send(String message) {
        this.plugin.getLogger().info("[Match] " + ChatColor.stripColor(message));

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.send(message);
        }
    }

    public void sendGoalMessage(String message) {
        this.plugin.getLogger().info("[Goal] " + ChatColor.stripColor(message));

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.send(ChatColor.YELLOW + message);
            player.sendAction(ChatColor.YELLOW + message);
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

    public void setPlayWindow(PlayMatchWindow playWindow) {
        this.playWindow = playWindow;
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

        this.startTime = Time.now();

        this.broadcastStartMessage();
        this.setForceStart(force);
        this.setState(MatchState.RUNNING);

        for (ArcadePlayer online : this.plugin.getPlayers()) {
            GamePlayer player = online.getGamePlayer();
            if (player == null || this.isObserving(player)) {
                continue;
            }

            player.setParticipating(true);

            online.getPermissions().clearGroups();
            online.getPermissions().refresh();

            player.reset();
        }

        this.plugin.getEventBus().publish(new MatchStartedEvent(this.plugin, this, force));
    }

    public boolean unregisterDynamicWinnable(DynamicWinnable dynamicWinnable) {
        return this.dynamicWinnables.remove(dynamicWinnable);
    }

    public void unregisterWinner(MatchWinner winner) {
        this.winnerMap.remove(winner.getId());
    }

    public interface IObserverHandler {
        boolean isPlayerObserving(GamePlayer player);
    }
}
