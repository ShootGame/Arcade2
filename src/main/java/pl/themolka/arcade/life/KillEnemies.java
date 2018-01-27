package pl.themolka.arcade.life;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.SimpleInteractableGoal;

import java.util.HashSet;
import java.util.Set;

/**
 * The objective is to kill all the enemies in the given {@link Set}.
 */
public class KillEnemies extends SimpleInteractableGoal {
    public static final String DEFAULT_GOAL_NAME = "Kill Enemies";

    protected final KillEnemiesGame game;

    private final Set<GoalHolder> enemies;

    public KillEnemies(KillEnemiesGame game, GoalHolder owner, Set<GoalHolder> enemies) {
        super(game.getGame(), owner);
        this.game = game;

        this.enemies = enemies;
    }

    @Override
    protected void complete(GoalHolder completer) {
        GoalCompleteEvent.call(this.game.getPlugin(), this, completer);
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public double getProgress() {
        int allies = this.getOwner().getPlayers().size();
        Set<GamePlayer> enemies = this.getEnemyPlayers();
        return PROGRESS_SCORED / (allies + enemies.size()) * allies;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByOwner(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return (!this.getOwner().getPlayers().isEmpty() && this.getEnemyPlayers().isEmpty()) || super.isCompleted();
    }

    @Override
    public boolean isCompleted(GoalHolder completer) {
        return this.getOwner().equals(completer) && this.isCompleted();
    }

    @Override
    public boolean reset() {
        this.enemies.clear();
        return true;
    }

    public Set<GamePlayer> getEnemyPlayers() {
        Set<GamePlayer> players = new HashSet<>();
        for (GoalHolder enemy : this.enemies) {
            players.addAll(enemy.getPlayers());
        }

        return players;
    }

    public void refreshCompletion() {
        if (this.isCompleted()) {
            this.complete(null);
        }
    }
}
