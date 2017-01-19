package pl.themolka.arcade.score;

import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.GoalScoreEvent;
import pl.themolka.arcade.match.MatchWinner;

public class Score implements Goal {
    private final ScoreGame game;

    private final MatchWinner owner;
    private boolean completed;
    private final int initScore;
    private int limit;
    private int score;
    private boolean scoreTouched;

    public Score(ScoreGame game, MatchWinner owner) {
        this(game, owner, 0);
    }

    public Score(ScoreGame game, MatchWinner owner, int initScore) {
        this.game = game;

        this.owner = owner;
        this.initScore = initScore;
        this.score = initScore;
    }

    @Override
    public String getName() {
        return "Score";
    }

    /**
     * Current progress of this score.
     * This method may return a percentage of this goal it it has a limit,
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
    public boolean isCompletableBy(MatchWinner winner) {
        return this.getOwner().equals(winner);
    }

    @Override
    public boolean isCompleted() {
        return this.completed || (this.hasLimit() && this.getScore() >= this.getLimit());
    }

    @Override
    public boolean isCompleted(MatchWinner winner) {
        return this.isCompletableBy(winner) && this.isCompleted();
    }

    @Override
    public boolean isUntouched() {
        return !this.scoreTouched;
    }

    @Override
    public boolean reset() {
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
    public void setCompleted(MatchWinner winner, boolean completed) {
        if (completed) {
            this.handleGoalScored();
        } else {
            this.reset();
        }
    }

    public MatchWinner getOwner() {
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

    public boolean isScoreTouched() {
        return this.scoreTouched;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Events called in this method:
     *   - ScoreIncrementEvent (cancelable)
     *   - GoalProgressEvent
     *   ... and if this goal is being completed:
     *     - ScoreScoredEvent (cancelable)
     *     - GoalScoreEvent (cancelable)
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
            this.handleGoalScored();
        }
    }

    private void handleGoalScored() {
        if (this.completed) {
            return;
        }
        this.completed = true;

        ScoreScoredEvent event = new ScoreScoredEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            // This game for this `MatchWinner` has been scored - we can tell it
            // to the plugin, so it can end the match. This method will loop all
            // `MatchWinner`s (like players or teams) to find the winner.
            this.game.getPlugin().getEventBus().publish(new GoalScoreEvent(this.game.getPlugin(), this));
        }
    }
}
