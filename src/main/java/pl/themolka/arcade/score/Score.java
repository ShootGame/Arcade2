package pl.themolka.arcade.score;

import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;

public class Score implements Goal {
    public static final String DEFAULT_GOAL_NAME = "Score";

    private final ScoreGame game;

    private final GoalHolder owner;
    private boolean completed;
    private final int initScore;
    private int limit;
    private String name;
    private int score;
    private boolean scoreTouched;

    public Score(ScoreGame game, GoalHolder owner) {
        this(game, owner, 0);
    }

    public Score(ScoreGame game, GoalHolder owner, int initScore) {
        this.game = game;

        this.owner = owner;
        this.initScore = initScore;
        this.score = initScore;
    }

    @Override
    public String getName() {
        if (this.hasName()) {
            return this.name;
        }

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
    public boolean isCompletableBy(GoalHolder holder) {
        return this.getOwner().equals(holder);
    }

    @Override
    public boolean isCompleted() {
        return this.completed || (this.isLimitReached());
    }

    @Override
    public boolean isUntouched() {
        return !this.scoreTouched;
    }

    @Override
    public boolean reset() {
        if (!this.isCompleted()) {
            return false;
        }

        ScoreResetEvent event = new ScoreResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.game.getPlugin().getEventBus().publish(new GoalResetEvent(this.game.getPlugin(), this));

            this.completed = false;
            this.scoreTouched = false;
            this.score = this.getInitScore();
            return true;
        }

        return false;
    }

    @Override
    public void setCompleted(GoalHolder holder, boolean completed) {
        if (completed) {
            this.handleGoalComplete();
        } else {
            this.reset();
        }
    }

    public GoalHolder getOwner() {
        return this.owner;
    }

    public int getInitScore() {
        return this.initScore;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getScore() {
        return this.score;
    }

    public boolean hasLimit() {
        return this.score != ScoreModule.LIMIT_NULL;
    }

    public boolean hasName() {
        return this.name != null;
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
    public void incrementScore(int points) {
        ScoreIncrementEvent event = new ScoreIncrementEvent(this.game.getPlugin(), this, points);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        double oldProgress = this.getProgress();

        this.scoreTouched = true;
        this.score += event.getPoints();

        this.game.getPlugin().getEventBus().publish(new GoalProgressEvent(this.game.getPlugin(), this, oldProgress));

        if (this.isCompleted()) {
            this.handleGoalComplete();
        }
    }

    public boolean isLimitReached() {
        return this.hasLimit() && this.getScore() >= this.getLimit();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void handleGoalComplete() {
        if (this.completed) {
            return;
        }
        this.completed = true;

        if (this.isLimitReached()) {
            this.game.getPlugin().getEventBus().publish(new ScoreLimitReachEvent(this.game.getPlugin(), this));
        }

        ScoreScoredEvent event = new ScoreScoredEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            // This game for this `GoalHolder` has been completed - we can tell
            // it to the plugin, so it can end the game. This method will loop
            // all `GoalHolder`s (like players or teams) to find the winner.
            this.game.getPlugin().getEventBus().publish(new GoalCompleteEvent(this.game.getPlugin(), this));
        }
    }
}
