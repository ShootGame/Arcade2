package pl.themolka.arcade.life;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.SimpleInteractableGoal;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The objective is to kill all the enemies in the given {@link Set}.
 */
public class KillEnemies extends SimpleInteractableGoal {
    public static final String DEFAULT_GOAL_NAME = "Kill Enemies";

    private final Set<Participator> enemies;

    @Deprecated
    public KillEnemies(Game game, Participator owner, Set<Participator> enemies) {
        super(game, owner);

        this.enemies = enemies;
    }

    protected KillEnemies(Game game, Config config) {
        super(game, config.owner().get());

        this.enemies = new LinkedHashSet<>();
        for (Ref<Participator> enemy : config.enemies()) {
            this.enemies.add(enemy.get());
        }
    }

    @Override
    protected void complete(Participator completer) {
        if (!super.isCompleted()) {
            GoalCompleteEvent.call(this.getGame().getPlugin(), this, completer);
        }
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
        Set<GamePlayer> players = new LinkedHashSet<>();
        for (Participator enemy : this.enemies) {
            players.addAll(enemy.getPlayers());
        }

        return players;
    }

    public interface Config extends IGameConfig<KillEnemies> {
        Ref<Participator> owner();
        Set<Ref<Participator>> enemies();

        @Override
        default KillEnemies create(Game game) {
            return new KillEnemies(game, this);
        }
    }
}
