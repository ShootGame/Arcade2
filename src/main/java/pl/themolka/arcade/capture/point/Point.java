package pl.themolka.arcade.capture.point;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.CapturingCapturedState;
import pl.themolka.arcade.capture.point.state.CapturingState;
import pl.themolka.arcade.capture.point.state.LosingState;
import pl.themolka.arcade.capture.point.state.NeutralState;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.score.ScoreGame;
import pl.themolka.arcade.score.ScoreModule;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Point extends Capturable {
    public static final Time DEFAULT_CAPTURE_TIME = Time.ofSeconds(10);
    public static final String DEFAULT_GOAL_NAME = "Point";
    public static final Time DEFAULT_LOSE_TIME = Time.ofSeconds(10);

    public static final Time HEARTBEAT_INTERVAL = Time.ofTicks(5); // 1/4 of a second

    private Region captureRegion;
    private Time captureTime = DEFAULT_CAPTURE_TIME;
    private boolean capturingCapturedEnabled; // false if the point should be neutral to be captured.
    private Time loseTime = DEFAULT_LOSE_TIME;
    private boolean permanent = false;
    private final Set<GamePlayer> players = new HashSet<>();
    private int pointReward;
    private boolean pointTouched = false;
    private PointState state;
    private Region stateRegion;

    public Point(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Point(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        if (owner != null) {
            this.state = this.createCapturedState();
        } else {
            this.state = this.createNeutralState();
        }
    }

    @Override
    public void capture(GoalHolder completer) {
        this.setOwner(completer);

        String message = ChatColor.GOLD.toString() + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                completer.getTitle() + ChatColor.RESET + ChatColor.YELLOW + ".";

        this.game.getMatch().sendGoalMessage(message);
        this.setCompleted(completer, true);
    }

    @Override
    public String getColoredName() {
        return this.getState().getColor() + this.getName();
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        if (this.isNeutral()) {
            // Neutral points are not announced.
            return null;
        }

        return ChatColor.GOLD + interact + ChatColor.YELLOW + " lost " +
                ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    @Override
    public double getProgress() {
        return this.getState().getProgress();
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.isCompletableByEveryone();
    }

    @Override
    public boolean isUntouched() {
        return !this.pointTouched;
    }

    @Override
    public boolean registerGoal() {
        return false;
    }

    @Override
    public void resetCapturable() {
        this.players.clear();
        this.pointTouched = false;
        this.state = this.createNeutralState();
    }

    public boolean addPlayer(GamePlayer player) {
        return this.players.add(player);
    }

    public CapturedState createCapturedState() {
        return new CapturedState(this);
    }

    public CapturedState createCapturedState(CapturingState capturing) {
        return new CapturedState(capturing);
    }

    public NeutralState createNeutralState() {
        return new NeutralState(this);
    }

    public NeutralState createNeutralState(LosingState losing) {
        return new NeutralState(losing);
    }

    public Region getCaptureRegion() {
        return this.captureRegion;
    }

    public Time getCaptureTime() {
        return this.captureTime;
    }

    public Time getLoseTime() {
        return this.loseTime;
    }

    public List<GamePlayer> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public int getPointReward() {
        return this.pointReward;
    }

    public PointState getState() {
        return this.state;
    }

    public Region getStateRegion() {
        return this.stateRegion;
    }

    public boolean hasPlayer(GamePlayer player) {
        return this.players.contains(player);
    }

    // This method is executed synchronously every 5 ticks.
    public void heartbeat(long ticks) {
        if (this.isPermanent() && this.isCaptured()) {
            return;
        }

        Match match = this.game.getMatch();

        Multimap<GoalHolder, GamePlayer> competitors = ArrayListMultimap.create();
        for (GamePlayer player : this.getPlayers()) {
            if (!this.isCompletableBy(player)) {
                continue;
            }

            GoalHolder competitor = match.findWinnerByPlayer(player); // This is not the best way to find registered GoalHolders.
            if (competitor != null && (competitor.equals(player) || this.isCompletableBy(competitor))) {
                competitors.put(competitor, player);
            }
        }

        Multimap<GoalHolder, GamePlayer> dominators = ArrayListMultimap.create();
        for (Map.Entry<GoalHolder, Collection<GamePlayer>> entry : competitors.asMap().entrySet()) {
            GoalHolder competitor = entry.getKey();
            Collection<GamePlayer> players = entry.getValue();

            int playerCount = players.size();
            int dominatorCount = 0;

            for (Map.Entry<GoalHolder, Collection<GamePlayer>> dominator : dominators.asMap().entrySet()) {
                // All competitors have same player counts - break the loop.
                dominatorCount = dominator.getValue().size();
                break;
            }

            if (playerCount < dominatorCount) {
                continue;
            } else if (playerCount > dominatorCount) {
                // Do not clear the map when player counts are equal.
                dominators.clear();
            }

            dominators.putAll(competitor, players);
        }

        // Heartbeat the current state.
        GoalHolder owner = this.getOwner();
        this.getState().heartbeat(ticks, match, competitors, dominators, owner);

        if (owner != null && ticks % Time.ofSeconds(1).toTicks() == 0) {
            // Give reward points for owning the point.
            this.heartbeatReward(owner, this.getPointReward());
        }
    }

    public void heartbeatReward(GoalHolder competitor, int points) {
        if (competitor != null && points != 0) {
            Score score = this.getScore(competitor);

            if (score != null) {
                PointPointsEvent event = new PointPointsEvent(this.game.getPlugin(), this, score, points);
                this.game.getPlugin().getEventBus().publish(event);

                int toGive = event.getPoints();
                if (toGive != 0 && !event.isCanceled()) {
                    score.incrementScore(competitor, toGive);
                }
            }
        }
    }

    public boolean isCapturingCapturedEnabled() {
        return this.capturingCapturedEnabled;
    }

    public boolean isNeutral() {
        return this.state instanceof NeutralState;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public void lose(GoalHolder loser) {
        String message = this.getGoalInteractMessage(loser.getTitle());
        if (message != null) {
            this.game.getMatch().sendGoalMessage(message);
        }

        this.setOwner(null);
    }

    public boolean removePlayer(GamePlayer player) {
        return this.players.remove(player);
    }

    public void setCaptureRegion(Region captureRegion) {
        this.captureRegion = captureRegion;
    }

    public void setCaptureTime(Time captureTime) {
        this.captureTime = captureTime;
    }

    public void setCapturingCapturedEnabled(boolean capturingCapturedEnabled) {
        this.capturingCapturedEnabled = capturingCapturedEnabled;
    }

    public void setLoseTime(Time loseTime) {
        this.loseTime = loseTime;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public void setPointReward(int pointReward) {
        this.pointReward = pointReward;
    }

    public void setState(PointState state) {
        this.state = state;
    }

    public void setStateRegion(Region stateRegion) {
        this.stateRegion = stateRegion;
    }

    public PointState startCapturing(GoalHolder capturing, double oldProgress) {
        CapturingState capturingState = new CapturingState(this, capturing);
        capturingState.setProgress(oldProgress); // ZERO?

        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), capturingState));
    }

    public PointState startCapturingCaptured(GoalHolder capturing, double oldProgress) {
        CapturingCapturedState capturingState = new CapturingCapturedState(this, capturing);
        capturingState.setProgress(oldProgress); // ZERO?

        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), capturingState));
    }

    public PointState startLosing(GoalHolder loser, double oldProgress) {
        LosingState losingState = new LosingState(this, loser);
        losingState.setProgress(oldProgress); // DONE?

        return this.startState(new PointLosingEvent(this.game.getPlugin(), this, this.getState(), losingState));
    }

    private Score getScore(GoalHolder holder) {
        GameModule module = this.game.getGame().getModule(ScoreModule.class);
        if (module != null && module instanceof ScoreGame) {
            return ((ScoreGame) module).getScore(holder);
        }

        return null;
    }

    private PointState startState(PointStateEvent event) {
        this.game.getPlugin().getEventBus().publish(event);
        if (event instanceof Cancelable && ((Cancelable) event).isCanceled()) {
            return null;
        }

        PointState newState = event.getNewState();
        this.setState(newState);
        return newState;
    }

    //
    // Track Players
    //

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GamePlayer player = this.fetchPlayer(event.getEntity());
        if (player != null) {
            this.removePlayer(player);
        }
    }

    // We are unable to use PlayerInitialSpawnEvent because our player sessions
    // are created or restored in the PlayerJoinEvent.
    @Handler(priority = Priority.LAST)
    public void onPlayerInitialSpawn(PlayerJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (this.shouldTrack(player, event.getBukkitPlayer().getLocation())) {
            this.addPlayer(player);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.onPlayerMove(event.getGamePlayer(), event.getTo());
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removePlayer(event.getGamePlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove(this.fetchPlayer(event.getPlayer()), event.getTo());
    }

    private GamePlayer fetchPlayer(Player bukkit) {
        return bukkit != null ? this.game.getGame().getPlayer(bukkit) : null;
    }

    private void onPlayerMove(GamePlayer player, Location to) {
        if (player != null) {
            if (this.shouldTrack(player, to)) {
                this.addPlayer(player);
                return;
            }

            this.removePlayer(player);
        }
    }

    private boolean shouldTrack(GamePlayer player, Location at) {
        if (player != null && player.isParticipating() && this.getCaptureRegion().contains(at)) {
            Player bukkit = player.getBukkit();
            return bukkit != null && !bukkit.isDead();
        }

        return false;
    }
}
