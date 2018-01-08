package pl.themolka.arcade.capture.point;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalLoseEvent;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFinder;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.score.ScoreGame;
import pl.themolka.arcade.score.ScoreModule;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Point extends Capturable {
    public static final RegionFinder DEFAULT_CAPTURE_REGION_FINDER = RegionFinder.EXACT;
    public static final Time DEFAULT_CAPTURE_TIME = Time.ofSeconds(10);
    public static final String DEFAULT_GOAL_NAME = "Point";
    public static final Time DEFAULT_LOSE_TIME = Time.ofSeconds(10);
    public static final Color DEFAULT_NEUTRAL_COLOR = Color.GOLD;

    public static final Time HEARTBEAT_INTERVAL = Time.ofTicks(1);

    private Filter captureFilter = Filters.undefined();
    private Region captureRegion;
    private RegionFinder captureRegionFinder = DEFAULT_CAPTURE_REGION_FINDER;
    private Time captureTime = DEFAULT_CAPTURE_TIME;
    private boolean capturingCapturedEnabled; // false if the point should be neutral to be captured.
    private Filter dominateFilter = Filters.undefined();
    private final PointState initialState;
    private Time loseTime = DEFAULT_LOSE_TIME;
    private Color neutralColor = DEFAULT_NEUTRAL_COLOR;
    private boolean objective;
    private boolean permanent = false;
    private final Set<GamePlayer> players = new HashSet<>();
    private double pointReward;
    private boolean pointTouched = false;
    private PointState state;
    private Region stateRegion;

    public Point(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Point(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        if (owner != null) {
            this.initialState = this.createCapturedState();
        } else {
            this.initialState = this.createNeutralState();
        }

        // Set the current state to a copy the initial state.
        this.state = this.getInitialState().copy();
    }

    @Override
    public void capture(GoalHolder completer, GamePlayer player /* null */) {
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

        return ChatColor.GOLD + interact + ChatColor.YELLOW + " has lost " +
                ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    @Override
    public double getProgress() {
        return this.getState().getProgress();
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByEveryone();
    }

    @Override
    public boolean isUntouched() {
        return !this.pointTouched;
    }

    @Override // Register goals only when it is an map objective.
    public boolean registerGoal() {
        return this.isObjective();
    }

    @Override
    public void resetCapturable() {
        this.players.clear();
        this.pointTouched = false;
        this.state = this.getInitialState().copy();
    }

    public boolean addPlayer(GamePlayer player) {
        return this.players.add(player);
    }

    public boolean canCapture(GoalHolder competitor) {
        return competitor != null && this.isCompletableBy(competitor) &&
                this.getCaptureFilter().filter(competitor).isNotDenied();
    }

    public boolean canDominate(GamePlayer player) {
        return player != null && player.isParticipating() && this.isCompletableBy(player) &&
                this.getDominateFilter().filter(player).isNotDenied();
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

    public Filter getCaptureFilter() {
        return this.captureFilter;
    }

    public Region getCaptureRegion() {
        return this.captureRegion;
    }

    public RegionFinder getCaptureRegionFinder() {
        return this.captureRegionFinder;
    }

    public Time getCaptureTime() {
        return this.captureTime;
    }

    public Filter getDominateFilter() {
        return this.dominateFilter;
    }

    public PointState getInitialState() {
        return this.initialState;
    }

    public Time getLoseTime() {
        return this.loseTime;
    }

    public Color getNeutralColor() {
        return this.neutralColor;
    }

    public List<GamePlayer> getPlayers() {
        return new ArrayList<>(this.players);
    }

    public double getPointReward() {
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

    public void heartbeat(long ticks) {
        if (this.isPermanent() && this.isCaptured()) {
            return;
        }

        Match match = this.game.getMatch();

        Multimap<GoalHolder, GamePlayer> competitors = ArrayListMultimap.create();
        for (GamePlayer player : this.getPlayers()) {
            if (this.canDominate(player)) {
                GoalHolder competitor = match.findWinnerByPlayer(player);
                // ^ This is not the best way to find registered GoalHolders every server tick.

                if (competitor != null && (competitor.equals(player) || this.isCompletableBy(competitor))) {
                    competitors.put(competitor, player);
                }
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

        List<GoalHolder> canCapture = new ArrayList<>();
        for (GoalHolder competitor : dominators.keySet()) {
            if (this.canCapture(competitor)) {
                canCapture.add(competitor);
            }
        }

        // Heartbeat the current state.
        GoalHolder owner = this.getOwner();
        this.getState().heartbeat(ticks, match, competitors, dominators, canCapture, owner);

        double pointReward = this.getPointReward();
        if (owner != null && pointReward != Score.ZERO) {
            // Give reward points for owning the point.
            this.heartbeatReward(owner, pointReward);
        }
    }

    public void heartbeatReward(GoalHolder competitor, double pointReward /* this is per second */) {
        if (competitor != null && pointReward != Score.ZERO) {
            Score score = this.getScore(competitor);
            double toGive = (HEARTBEAT_INTERVAL.toTicks() / Time.SECOND.toTicks()) * pointReward;

            if (score != null && toGive != Score.ZERO) {
                PointPointsEvent event = new PointPointsEvent(this.game.getPlugin(), this, score, toGive);
                this.game.getPlugin().getEventBus().publish(event);

                double newToGive = event.getPoints();
                if (newToGive != Score.ZERO && !event.isCanceled()) {
                    score.incrementScore(competitor, newToGive);
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

    public boolean isObjective() {
        return this.objective;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public void lose(GoalHolder loser) {
        String message = this.getGoalInteractMessage(loser.getTitle());
        if (message != null) {
            this.game.getMatch().sendGoalMessage(message);
        }

        this.captured = false;
        this.capturedBy = null;
        this.getContributions().clearContributors();
        this.setOwner(null);

        GoalLoseEvent.call(this.game.getPlugin(), this, loser);
    }

    public boolean playerEnter(GamePlayer player) {
        if (this.players.contains(player)) {
            return false;
        }

        PointPlayerEnterEvent event = new PointPlayerEnterEvent(this.game.getPlugin(), this, player);
        this.game.getPlugin().getEventBus().publish(event);

        return this.addPlayer(event.getPlayer());
    }

    public boolean playerLeave(GamePlayer player) {
        if (!this.players.contains(player)) {
            return false;
        }

        PointPlayerLeaveEvent event = new PointPlayerLeaveEvent(this.game.getPlugin(), this, player);
        this.game.getPlugin().getEventBus().publish(event);

        return this.removePlayer(event.getPlayer());
    }

    public boolean removePlayer(GamePlayer player) {
        return this.players.remove(player);
    }

    public void setCaptureFilter(Filter captureFilter) {
        this.captureFilter = captureFilter;
    }

    public void setCaptureRegion(Region captureRegion) {
        this.captureRegion = captureRegion;
    }

    public void setCaptureRegionFinder(RegionFinder captureRegionFinder) {
        this.captureRegionFinder = captureRegionFinder;
    }

    public void setCaptureTime(Time captureTime) {
        this.captureTime = captureTime;
    }

    public void setCapturingCapturedEnabled(boolean capturingCapturedEnabled) {
        this.capturingCapturedEnabled = capturingCapturedEnabled;
    }

    public void setDominateFilter(Filter dominateFilter) {
        this.dominateFilter = dominateFilter;
    }

    public void setLoseTime(Time loseTime) {
        this.loseTime = loseTime;
    }

    public void setNeutralColor(Color color) {
        this.neutralColor = color;
    }

    public void setObjective(boolean objective) {
        this.objective = objective;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public void setPointReward(double pointReward) {
        this.pointReward = pointReward;
    }

    public void setState(PointState state) {
        this.state = state;
    }

    public void setStateRegion(Region stateRegion) {
        this.stateRegion = stateRegion;
    }

    public PointState startCapturing(GoalHolder capturer, double oldProgress) {
        CapturingState capturingState = new CapturingState(this, capturer);
        capturingState.setProgress(oldProgress); // ZERO?

        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), capturingState));
    }

    public PointState startCapturingCaptured(GoalHolder capturer, double oldProgress) {
        CapturingCapturedState capturingState = new CapturingCapturedState(this, capturer);
        capturingState.setProgress(oldProgress); // ZERO?

        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), capturingState));
    }

    public PointState startLosing(GoalHolder loser, double oldProgress) {
        LosingState losingState = new LosingState(this, loser);
        losingState.setProgress(oldProgress); // DONE?

        return this.startState(new PointLosingEvent(this.game.getPlugin(), this, this.getState(), losingState));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("captured", this.isCaptured())
                .append("capturedBy", this.getCapturedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("captureRegionFinder", this.captureRegionFinder)
                .append("captureTime", this.captureTime)
                .append("capturingCapturedEnabled", this.capturingCapturedEnabled)
                .append("loseTime", this.loseTime)
                .append("permanent", this.permanent)
                .append("pointReward", this.pointReward)
                .append("pointTouched", this.pointTouched)
                .build();
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
            this.playerLeave(player);
        }
    }

    // We are unable to use PlayerInitialSpawnEvent because our player sessions
    // are created or restored in the PlayerJoinEvent.
    @Handler(priority = Priority.LAST)
    public void onPlayerInitialSpawn(PlayerJoinEvent event) {
        GamePlayer player = event.getGamePlayer();
        if (this.shouldTrack(player, event.getBukkitPlayer().getLocation())) {
            this.playerEnter(player);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.onPlayerMove(event.getGamePlayer(), event.getTo());
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerLeave(event.getGamePlayer());
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
                this.playerEnter(player);
            } else {
                this.playerLeave(player);
            }
        }
    }

    private boolean shouldTrack(GamePlayer player, Location at) {
        if (player == null || at == null) {
            return false;
        }

        if (this.getCaptureRegionFinder().regionContains(this.getCaptureRegion(), at)) {
            Player bukkit = player.getBukkit();
            return bukkit != null && !bukkit.isDead();
        }

        return false;
    }
}
