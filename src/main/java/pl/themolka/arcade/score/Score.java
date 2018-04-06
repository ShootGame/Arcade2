package pl.themolka.arcade.score;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleGoal;
import pl.themolka.arcade.match.Match;

public class Score extends SimpleGoal {
    public static final String DEFAULT_GOAL_NAME = "Score";
    public static final double ZERO = 0.0D;
    public static final double MIN = Double.MIN_VALUE;
    public static final double MAX = Double.MAX_VALUE;

    private final double deathLoss;
    private final double initialScore;
    private final double killReward;
    private final double limit;

    private Match match;
    private double score;

    protected Score(Game game, IGameConfig.Library library, Config config) {
        super(game, library.getOrDefine(game, config.owner().get()));
        super.setName(config.name());

        this.deathLoss = config.deathLoss();
        this.initialScore = config.initialScore();
        this.killReward = config.killReward();
        this.limit = config.limit();
        this.score = config.initialScore();
    }

    @Override
    public void complete(Participator completer) {
        boolean byLimit = this.isLimitReached();

        ScoreScoredEvent event = new ScoreScoredEvent(this.getPlugin(), this, byLimit, completer);
        this.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            if (byLimit) {
                this.getPlugin().getEventBus().publish(new ScoreLimitReachEvent(this.getPlugin(), this));
            }

            GoalCompleteEvent.call(this.getPlugin(), this, event.getCompleter());
        }
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    /**
     * Current progress of this score.
     * This method may return a percentage of this goal if it has a limit,
     * or #PROGRESS_UNTOUCHED of not.
     * @return a double between 0 (0% - untouched) and 1 (100% - completed).
     */
    @Override
    public double getProgress() {
        if (!this.hasLimit()) {
            return Goal.PROGRESS_UNTOUCHED;
        }

        double progress = this.getScore() / this.limit;
        if (progress < Goal.PROGRESS_UNTOUCHED) {
            return Goal.PROGRESS_UNTOUCHED;
        } else if (progress > Goal.PROGRESS_SCORED) {
            return Goal.PROGRESS_SCORED;
        }

        return progress;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByOwner(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || this.isLimitReached();
    }

    @Override
    public boolean reset() {
        ScoreResetEvent event = new ScoreResetEvent(this.getPlugin(), this);
        this.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.getPlugin(), this);

            this.score = this.initialScore;
            this.setCompleted(false);
            this.setTouched(false);
            return true;
        }

        return false;
    }

    public double getDeathLoss() {
        return this.deathLoss;
    }

    public double getKillReward() {
        return this.killReward;
    }

    public double getLimit() {
        return this.limit;
    }

    public double getScore() {
        return this.score;
    }

    public boolean hasLimit() {
        return this.limit != Score.MAX;
    }

    /**
     * Events called in this method:
     *   - ScoreIncrementEvent (cancelable)
     *   - GoalProgressEvent
     *   ... and if this goal is being completed:
     *     ... if this goal has reached the score limit:
     *       - ScoreLimitReachEvent
     *     - ScoreScoredEvent (cancelable)
     *     - GoalCompleteEvent (cancelable)
     */
    public void incrementScore(Participator completer, double points) {
        if (!this.match.isRunning() || isValid(this.getScore() + points)) {
            return;
        }

        ScoreIncrementEvent event = new ScoreIncrementEvent(this.getPlugin(), this, completer, points);
        this.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        double oldProgress = this.getProgress();
        double newScore = this.getScore() + points;

        if (!isValid(newScore)) {
            this.score = newScore;
            this.setTouched(true);

            GoalProgressEvent.call(this.getPlugin(), this, event.getCompleter(), oldProgress);

            if (this.isCompleted()) {
                this.setCompleted(event.getCompleter());
            }
        }
    }

    public boolean isLimitReached() {
        return this.hasLimit() && this.score >= this.limit;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public static boolean outOfBounds(double score) {
        return score <= MIN || score >= MAX;
    }

    public static boolean isValid(double score) {
        return !outOfBounds(score) && score != ZERO;
    }

    public interface Config extends IGameConfig<Score> {
        double DEFAULT_DEATH_LOSS = Score.ZERO;
        double DEFAULT_INITIAL_SCORE = Score.ZERO;
        double DEFAULT_KILL_REWARD = Score.ZERO;
        double DEFAULT_LIMIT = Score.MAX;

        double deathLoss();
        double initialScore();
        double killReward();
        double limit();
        String name();
        Ref<Participator.Config<?>> owner();

        @Override
        default Score create(Game game, Library library) {
            return new Score(game, library, this);
        }
    }
}
