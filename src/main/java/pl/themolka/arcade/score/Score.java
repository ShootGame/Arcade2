package pl.themolka.arcade.score;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;

public class Score implements Goal {
    public static final String DEFAULT_GOAL_NAME = "Score";

    protected final ScoreGame game;

    private final GoalHolder owner;
    private final int initScore;
    private int limit;
    private String name;
    private int score;
    private boolean scored;
    private GoalHolder scoredBy;
    private boolean scoreTouched = false;

    public Score(ScoreGame game, GoalHolder owner) {
        this(game, owner, 0);
    }

    public Score(ScoreGame game, GoalHolder owner, int initScore) {
        this.game = game;

        this.owner = owner;
        this.initScore = initScore;
        this.score = initScore;
        this.scored = false;
    }

    @Override
    public String getColoredName() {
        return this.owner.getColor().toChat() + this.getName();
    }

    @Override
    public Game getGame() {
        return this.game.getGame();
    }

    @Override
    public String getName() {
        if (this.hasName()) {
            return this.name;
        }

        return DEFAULT_GOAL_NAME;
    }

    @Override
    public GoalHolder getOwner() {
        return this.owner;
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
        return Goal.isCompletableByPositive(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return this.scored || this.isLimitReached();
    }

    @Override
    public boolean isCompleted(GoalHolder completer) {
        return this.isCompleted() && (this.scoredBy == null || this.scoredBy.equals(completer));
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
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.score = this.getInitScore();
            this.scoreTouched = false;
            this.scored = false;
            this.scoredBy = null;
            return true;
        }

        return false;
    }

    @Override
    public void setCompleted(GoalHolder completer, boolean completed) {
        if (completed) {
            this.handleGoalComplete(completer);
        } else {
            this.reset();
        }
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
    public void incrementScore(GoalHolder completer, int points) {
        ScoreIncrementEvent event = new ScoreIncrementEvent(this.game.getPlugin(), this, completer, points);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        double oldProgress = this.getProgress();

        this.scoreTouched = true;
        this.score += event.getPoints();

        GoalProgressEvent.call(this.game.getPlugin(), this, event.getCompleter(), oldProgress);

        if (this.isCompleted()) {
            this.setCompleted(event.getCompleter(), true);
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

    private void handleGoalComplete(GoalHolder completer) {
        if (this.scored) {
            return;
        }

        boolean byLimit = this.isLimitReached();

        ScoreScoredEvent event = new ScoreScoredEvent(this.game.getPlugin(), this, byLimit, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.scored = true;
            this.scoredBy = completer;

            if (byLimit) {
                this.game.getPlugin().getEventBus().publish(new ScoreLimitReachEvent(this.game.getPlugin(), this));
            }

            // This game for this `GoalHolder` has been completed - we can tell
            // it to the plugin, so it can end the game. This method will loop
            // all `GoalHolder`s (like players or teams) to find the winner.
            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
        }
    }
}
