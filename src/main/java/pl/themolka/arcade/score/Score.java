package pl.themolka.arcade.score;

import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleGoal;

public class Score extends SimpleGoal {
    public static final String DEFAULT_GOAL_NAME = "Score";
    public static final double ZERO = 0.0D;
    public static final double MIN = Double.MIN_VALUE;
    public static final double MAX = Double.MAX_VALUE;

    protected final ScoreGame game;

    private final double initialScore;
    private double limit;
    private double score;

    public Score(ScoreGame game, GoalHolder owner) {
        this(game, owner, ZERO);
    }

    public Score(ScoreGame game, GoalHolder owner, double initialScore) {
        super(game.getGame(), owner);
        this.game = game;

        this.initialScore = initialScore;
        this.score = initialScore;
    }

    @Override
    public void complete(GoalHolder completer) {
        boolean byLimit = this.isLimitReached();

        ScoreScoredEvent event = new ScoreScoredEvent(this.game.getPlugin(), this, byLimit, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            if (byLimit) {
                this.game.getPlugin().getEventBus().publish(new ScoreLimitReachEvent(this.game.getPlugin(), this));
            }

            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
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

        double progress = this.getScore() / this.getLimit();
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
        ScoreResetEvent event = new ScoreResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.score = this.getInitialScore();
            this.setCompleted(false);
            this.setTouched(false);
            return true;
        }

        return false;
    }

    public double getInitialScore() {
        return this.initialScore;
    }

    public double getLimit() {
        return this.limit;
    }

    public double getScore() {
        return this.score;
    }

    public ScoreGame getScoreGame() {
        return this.game;
    }

    public boolean hasLimit() {
        return this.score != ScoreModule.LIMIT_NULL;
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
    public void incrementScore(GoalHolder completer, double points) {
        if (outOfBounds(this.getScore() + points)) {
            return;
        }

        ScoreIncrementEvent event = new ScoreIncrementEvent(this.game.getPlugin(), this, completer, points);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        double oldProgress = this.getProgress();
        double newScore = this.getScore() + points;

        if (!outOfBounds(newScore)) {
            this.score = newScore;
            this.setTouched(true);

            GoalProgressEvent.call(this.game.getPlugin(), this, event.getCompleter(), oldProgress);

            if (this.isCompleted()) {
                this.setCompleted(event.getCompleter());
            }
        }
    }

    public boolean isLimitReached() {
        return this.hasLimit() && this.getScore() >= this.getLimit();
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public static boolean outOfBounds(double score) {
        return score < MIN || score > MAX;
    }
}
