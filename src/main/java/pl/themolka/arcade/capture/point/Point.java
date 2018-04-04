package pl.themolka.arcade.capture.point;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.CapturingCapturedState;
import pl.themolka.arcade.capture.point.state.CapturingState;
import pl.themolka.arcade.capture.point.state.LosingState;
import pl.themolka.arcade.capture.point.state.NeutralState;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalLoseEvent;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.score.ScoreGame;
import pl.themolka.arcade.score.ScoreModule;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Point extends Capturable {
    public static final Time DEFAULT_CAPTURE_TIME = Time.ofSeconds(10);
    public static final Filter DEFAULT_DOMINATE_FILTER = Filters.undefined();
    public static final DominatorStrategy DEFAULT_DOMINATOR_STRATEGY = DominatorStrategy.LEAD;
    public static final String DEFAULT_GOAL_NAME = "Point";
    public static final Time DEFAULT_LOSE_TIME = Time.ofSeconds(10);
    public static final Color DEFAULT_NEUTRAL_COLOR = Color.GOLD;

    public static final Time HEARTBEAT_INTERVAL = Time.ofTicks(1);

    private PointCapture capture;
    private Time captureTime = DEFAULT_CAPTURE_TIME;
    private boolean capturingCapturedEnabled = false; // false if the point should be neutral to be captured.
    private Filter dominateFilter = DEFAULT_DOMINATE_FILTER;
    private DominatorStrategy dominatorStrategy;
    private final PointState initialState;
    private Time loseTime = DEFAULT_LOSE_TIME;
    private Color neutralColor = DEFAULT_NEUTRAL_COLOR;
    private boolean objective;
    private boolean permanent = false;
    private double pointReward;
    private PointState state;
    private Region stateRegion;

    public Point(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Point(CaptureGame game, Participator owner, String id) {
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
    public void capture(Participator completer, GamePlayer player /* null */) {
        this.setOwner(completer);

        String message = ChatColor.GOLD.toString() + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                completer.getTitle() + ChatColor.RESET + ChatColor.YELLOW + ".";

        this.game.getMatch().sendGoalMessage(message);
        this.setTouched(true);
        this.setCompleted(completer);
    }

    @Override
    public String getColoredName() {
        return this.getState().getColor() + this.getName() + ChatColor.RESET;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public List<Object> getEventListeners() {
        return Collections.singletonList(this.getCapture()); // Register player tracking
    }

    @Override
    public double getProgress() {
        return this.getState().getProgress();
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByEveryone();
    }

    @Override // Register goals only when it is an map objective.
    public boolean registerGoal() {
        return this.isObjective();
    }

    @Override
    public void resetCapturable() {
        PointCapture capture = this.capture;
        if (capture != null) {
            capture.clearPlayers();
        }

        this.state = this.getInitialState().copy();
    }

    public boolean canCapture(Participator competitor) {
        // Rebuild this if we would want to support
        // many PointCapture objects in the future.
        return competitor != null && this.isCompletableBy(competitor) &&
                this.getCapture().canCapture(competitor);

    }

    public boolean canDominate(GamePlayer player) {
        return player != null && player.isParticipating() && this.isCompletableBy(player) &&
                this.dominateFilter.filter(player).isNotFalse();
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

    public PointCapture getCapture() {
        return this.capture;
    }

    public Time getCaptureTime() {
        return this.captureTime;
    }

    public Filter getDominateFilter() {
        return this.dominateFilter;
    }

    public DominatorStrategy getDominatorStrategy() {
        return this.dominatorStrategy;
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

    public Set<GamePlayer> getPlayers() {
        PointCapture capture = this.getCapture();
        if (capture != null) {
            return capture.getPlayers();
        }

        return Collections.emptySet();
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

    public void heartbeat(long ticks) {
        if (this.isPermanent() && this.isCompleted()) {
            return;
        }

        Match match = this.game.getMatch();

        Multimap<Participator, GamePlayer> competitors = ArrayListMultimap.create();
        for (GamePlayer player : this.getPlayers()) {
            if (this.canCapture(player) && this.canDominate(player)) {
                Participator competitor = match.findWinnerByPlayer(player);
                // ^ This is not the best way to find registered Participators every server tick.

                if (competitor != null && (competitor.equals(player) || this.canCapture(competitor))) {
                    competitors.put(competitor, player);
                }
            }
        }

        Multimap<Participator, GamePlayer> dominators = this.getDominatorStrategy().getDominators(competitors);
        if (dominators == null) {
            dominators = DominatorStrategy.EMPTY_DOMINATORS;
        }

        List<Participator> canCapture = new ArrayList<>();
        for (Participator competitor : dominators.keySet()) {
            if (this.canCapture(competitor)) {
                canCapture.add(competitor);
            }
        }

        // Heartbeat the current state.
        Participator oldOwner = this.getOwner();
        this.getState().heartbeat(ticks, match, competitors, dominators, canCapture, oldOwner);
        Participator newOwner = this.getOwner();

        double pointReward = this.getPointReward();
        if (oldOwner != null && newOwner != null && oldOwner.equals(newOwner) && pointReward != Score.ZERO) {
            // Give reward points for owning the point.
            this.heartbeatReward(oldOwner, pointReward);
        }
    }

    public void heartbeatReward(Participator competitor, double pointReward /* this is per second */) {
        if (competitor != null && pointReward != Score.ZERO) {
            Score score = this.getScore(competitor);
            double toGive = ((double) HEARTBEAT_INTERVAL.toTicks() / (double) Time.SECOND.toTicks()) * pointReward;

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

    public void lose(Participator loser) {
        String message = this.getLostMessage(loser.getTitle());
        if (message != null) {
            this.game.getMatch().sendGoalMessage(message);
        }

        this.setCompleted(false);
        this.getContributions().clearContributors();
        this.setTouched(true);
        this.setOwner(null);

        GoalLoseEvent.call(this.game.getPlugin(), this, loser);
    }

    public void setCapture(PointCapture capture) {
        this.capture = capture;
    }

    public void setCaptureTime(Time captureTime) {
        this.captureTime = captureTime;
    }

    public void setCapturingCapturedEnabled(boolean capturingCapturedEnabled) {
        this.capturingCapturedEnabled = capturingCapturedEnabled;
    }

    public void setDominateFilter(Filter dominateFilter) {
        this.dominateFilter = dominateFilter != null ? dominateFilter : DEFAULT_DOMINATE_FILTER;
    }

    public void setDominatorStrategy(DominatorStrategy dominatorStrategy) {
        this.dominatorStrategy = dominatorStrategy != null ? dominatorStrategy : DEFAULT_DOMINATOR_STRATEGY;
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
        String from = this.state != null ? this.state.getStateName() : "unknown";
        String to = state != null ? state.getStateName() : "unknown";

        this.game.getLogger().info(this.getName() + " is transforming from " + from + " to " + to + "...");
        this.state = state;
    }

    public void setStateRegion(Region stateRegion) {
        this.stateRegion = stateRegion;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("completed", this.isCompleted())
                .append("completedBy", this.getCompletedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("touched", this.isTouched())
                .append("capture", this.capture)
                .append("capturingCapturedEnabled", this.capturingCapturedEnabled)
                .append("dominatorStrategy", this.dominatorStrategy)
                .append("loseTime", this.loseTime)
                .append("permanent", this.permanent)
                .append("pointReward", this.pointReward)
                .build();
    }

    private Score getScore(Participator holder) {
        GameModule module = this.game.getGame().getModule(ScoreModule.class);
        if (module != null && module instanceof ScoreGame) {
            return ((ScoreGame) module).getScore(holder);
        }

        return null;
    }

    //
    // Starting New States
    //

    public PointState startCapturing(CapturingState state, double oldProgress) {
        state.setProgress(oldProgress); // ZERO?
        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public PointState startCapturing(Participator capturer, double oldProgress) {
        return this.startCapturing(new CapturingState(this, capturer), oldProgress);
    }

    public PointState startCapturingCaptured(CapturingCapturedState state, double oldProgress) {
        state.setProgress(oldProgress); // ZERO?
        return this.startState(new PointCapturingEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public PointState startCapturingCaptured(Participator capturer, double oldProgress) {
        return this.startCapturingCaptured(new CapturingCapturedState(this, capturer), oldProgress);
    }

    public PointState startLosing(LosingState state, double oldProgress) {
        state.setProgress(oldProgress); // DONE?
        return this.startState(new PointLosingEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public PointState startLosing(Participator loser, double oldProgress) {
        return this.startLosing(new LosingState(this, loser), oldProgress);
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
    // Messages
    //

    public String getLostMessage(String loser) {
        if (this.isNeutral() || loser == null) {
            // Neutral points are not announced.
            return null;
        }

        return ChatColor.GOLD + loser + ChatColor.YELLOW + " has lost " +
                ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }
}
