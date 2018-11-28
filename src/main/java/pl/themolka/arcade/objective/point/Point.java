package pl.themolka.arcade.objective.point;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dominator.DefaultDominators;
import pl.themolka.arcade.dominator.Dominator;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalLoseEvent;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.objective.StatableObjective;
import pl.themolka.arcade.objective.point.state.Captured;
import pl.themolka.arcade.objective.point.state.Neutral;
import pl.themolka.arcade.objective.point.state.PointState;
import pl.themolka.arcade.objective.point.state.PointStateFactory;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.score.ScoreGame;
import pl.themolka.arcade.score.ScoreModule;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Tickable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Point extends StatableObjective<PointState> implements IPoint, Tickable {
    public static final Time TICK_INTERVAL = Time.TICK;

    private final Capture capture;
    private final Time captureTime;
    private final Filter dominateFilter;
    private final Dominator<Participator> dominatorStrategy;
    private final Time loseTime;
    private final Color neutralColor;
    private final boolean permanent;
    private final double pointReward;
    private final PointStateFactory stateFactory;
    private final AbstractRegion stateRegion;

    protected Point(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.capture = (Capture) library.getOrDefine(game, config.capture().get());
        this.captureTime = config.captureTime().get();
        this.dominateFilter = library.getOrDefine(game, config.dominateFilter().getIfPresent());
        this.dominatorStrategy = config.dominatorStrategy().get();
        this.loseTime = config.loseTime().get();
        this.neutralColor = config.neutralColor().get();
        this.permanent = config.permanent().get();
        this.pointReward = config.pointReward().get();
        this.stateRegion = library.getOrDefine(game, config.stateRegion().getIfPresent());

        this.capture.setup(this);

        this.stateFactory = new PointStateFactory(this);
    }

    @Override
    public void completeObjective(Participator completer, GamePlayer player) {
        this.getGame().sendGoalMessage(this.describeOwner() + this.describeObjective() +
                ChatColor.YELLOW + " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                player.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + ".");

        this.setOwner(completer);
        super.completeObjective(completer, player);
    }

    @Override
    public PointState defineInitialState() {
        return this.hasOwner() ? new Captured(this)
                               : new Neutral(this);
    }

    @Override
    public String getDefaultName() {
        return Config.DEFAULT_NAME;
    }

    @Override
    public List<Object> getEventListeners() {
        return Collections.singletonList(this.capture.getTracker());
    }

    @Override
    public Time getTickInterval() {
        return TICK_INTERVAL;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByEveryone();
    }

    @Override
    public void onTick(long tick) {
        if (this.permanent && this.isCompleted()) {
            return;
        }

        MatchGame module = (MatchGame) this.getGame().getModule(MatchModule.class);
        if (module == null) {
            return;
        }

        Match match = module.getMatch();
        if (!match.isRunning()) {
            return;
        }

        Multimap<Participator, GamePlayer> participators = ArrayListMultimap.create();
        for (GamePlayer player : this.capture.getTracker().getTracking()) {
            if (this.canCapture(player) && this.canDominate(player)) {
                Participator participator = match.resolve(player);

                if (participator != null && (participator.equals(player) || this.canCapture(participator))) {
                    participators.put(participator, player);
                }
            }
        }

        Multimap<Participator, GamePlayer> dominators = this.resolveDominators(participators);

//        Multimap<Participator, GamePlayer> dominators = Nulls.defaults(
//                this.dominatorStrategy.getDominators(ImmutableMultimap.copyOf(participators)),
//                ImmutableMultimap.of());

        Participator oldOwner = this.getOwner();
        this.tick(new Tick(tick, participators, dominators, oldOwner));

        Participator newOwner = this.getOwner();
        if (oldOwner != null && Objects.equals(oldOwner, newOwner) && this.pointReward != Score.ZERO) {
            // Give reward points for owning the point.
            this.tickPointReward(oldOwner, this.pointReward);
        }
    }

    private Multimap<Participator, GamePlayer> resolveDominators(Multimap<Participator, GamePlayer> participators) {
        Map<Participator, Integer> input = new HashMap<>();
        for (Map.Entry<Participator, GamePlayer> entry : participators.entries()) {
            Participator participator = entry.getKey();
            input.put(participator, input.getOrDefault(participator, 1));
        }

        Map<Participator, Integer> dominators = this.dominatorStrategy.getDominators(input);
        if (dominators == null) {
            return ImmutableMultimap.of();
        }

        Multimap<Participator, GamePlayer> results = ArrayListMultimap.create();
        for (Map.Entry<Participator, Integer> dominator : dominators.entrySet()) {
            Participator participator = dominator.getKey();
            results.putAll(participator, participators.get(participator));
        }

        return ImmutableMultimap.copyOf(results);
    }

    public void tickPointReward(Participator owner, double pointReward) {
        if (owner == null || pointReward == Score.ZERO) {
            return;
        }

        ScoreGame module = (ScoreGame) this.getGame().getModule(ScoreModule.class);
        if (module == null) {
            return;
        }

        Score score = module.getScore(owner);
        if (score != null) {
            double toGive = ((double) TICK_INTERVAL.toTicks() / (double) Time.SECOND.toTicks()) * pointReward;
            if (toGive != Score.ZERO) {
                score.incrementScore(owner, toGive);
            }
        }
    }

    public boolean canCapture(Participator participator) {
        return participator != null && participator.isParticipating() && this.isCompletableBy(participator) &&
                this.capture.canCapture(participator);
    }

    public boolean canDominate(GamePlayer player) {
        return player != null && player.isParticipating() && this.isCompletableBy(player) &&
                this.dominateFilter.filter(player).isNotFalse();
    }

    public Capture getCapture() {
        return this.capture;
    }

    public Time getCaptureTime() {
        return this.captureTime;
    }

    public Dominator getDominatorStrategy() {
        return this.dominatorStrategy;
    }

    public Time getLoseTime() {
        return this.loseTime;
    }

    public Color getNeutralColor() {
        return this.neutralColor;
    }

    public double getPointReward() {
        return this.pointReward;
    }

    public PointStateFactory getStateFactory() {
        return this.stateFactory;
    }

    public AbstractRegion getStateRegion() {
        return this.stateRegion;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public void loseObjective(Participator loser) {
        String message = this.createLoseMessage(loser);
        if (message != null) {
            this.getGame().sendGoalMessage(message);
        }

        this.setCompleted(false);
        this.getContributions().clearContributors();
        this.setTouched(true);
        this.setOwner(null);

        GoalLoseEvent.call(this, loser);
    }

    private String createLoseMessage(Participator loser) {
        if (loser == null) {
            // Neutral points are not announced.
            return null;
        }

        return ChatColor.GOLD + loser.getTitle() + ChatColor.YELLOW + " has lost " +
                this.describeObjective() + ChatColor.YELLOW + ".";
    }

    //
    // IPoint
    //

    @Override
    public Color getColor() {
        return this.getState().getColor();
    }

    @Override
    public FinitePercentage getProgress() {
        return this.getState().getProgress();
    }

    @Override
    public void tick(Tick tick) {
        this.getState().tick(tick);
    }

    public interface Config extends StatableObjective.Config<Point> {
        Time DEFAULT_CAPTURE_TIME = Time.ofSeconds(10);
        Dominator<Participator> DEFAULT_DOMINATOR_STRATEGY = DefaultDominators.getDefault();
        Time DEFAULT_LOSE_TIME = Time.ofSeconds(10);
        String DEFAULT_NAME = "Point";
        Color DEFAULT_NEUTRAL_COLOR = Color.GOLD;
        boolean DEFAULT_IS_OBJECTIVE = false;
        boolean DEFAULT_IS_PERMANENT = false;
        double DEFAULT_POINT_REWARD = Score.ZERO;

        Ref<Capture.Config> capture();
        default Ref<Time> captureTime() { return Ref.ofProvided(DEFAULT_CAPTURE_TIME); }
        default Ref<Filter.Config<?>> dominateFilter() { return Ref.empty(); }
        default Ref<Dominator<Participator>> dominatorStrategy() { return Ref.ofProvided(DEFAULT_DOMINATOR_STRATEGY); }
        default Ref<Time> loseTime() { return Ref.ofProvided(DEFAULT_LOSE_TIME); }
        default Ref<Color> neutralColor() { return Ref.ofProvided(DEFAULT_NEUTRAL_COLOR); }
        default Ref<Boolean> permanent() { return Ref.ofProvided(DEFAULT_IS_PERMANENT); }
        default Ref<Double> pointReward() { return Ref.ofProvided(DEFAULT_POINT_REWARD); }
        default Ref<AbstractRegion.Config<?>> stateRegion() { return Ref.empty(); }

        @Override
        default Point create(Game game, Library library) {
            return new Point(game, library, this);
        }
    }
}
