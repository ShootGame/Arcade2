package pl.themolka.arcade.score;

import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalScoreEvent;
import pl.themolka.arcade.match.MatchWinner;

public class Score implements Goal {
    private final ScoreGame game;

    private final MatchWinner owner;
    private final int initScore;
    private int limit = ScoreModule.LIMIT_NULL;
    private int score;
    private boolean scored;
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
        return this.getClass().getName();
    }

    @Override
    public double getProgress() {
        double progress = this.getScore() / this.getLimit();
        if (progress < Goal.PROGRESS_UNTOUCHED) {
            return Goal.PROGRESS_UNTOUCHED;
        } else if (progress > Goal.PROGRESS_SCORED) {
            return Goal.PROGRESS_SCORED;
        }

        return progress;
    }

    @Override
    public boolean isScorableBy(MatchWinner winner) {
        return !this.getOwner().equals(winner);
    }

    @Override
    public boolean isScored() {
        return this.getScore() >= this.getLimit();
    }

    @Override
    public boolean isScored(MatchWinner winner) {
        return this.scored || this.isScorableBy(winner) && this.isScored();
    }

    @Override
    public boolean isUntouched() {
        return !this.scoreTouched;
    }

    @Override
    public void setScored(MatchWinner winner, boolean scored) {
        this.score = this.getLimit();
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

    public boolean isScoreTouched() {
        return this.scoreTouched;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void incrementScore(int points) {
        ScoreIncrementEvent event = new ScoreIncrementEvent(this.game.getPlugin(), this, points);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        this.scoreTouched = true;
        this.score += event.getPoints();

        if (this.isScored()) {
            this.handleGoalScored();
        }
    }

    public void resetScore() {
        ScoreResetEvent event = new ScoreResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.scoreTouched = false;
            this.score = this.getInitScore();
        }
    }

    private void handleGoalScored() {
        if (this.scored) {
            return;
        }
        this.scored = true;

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
