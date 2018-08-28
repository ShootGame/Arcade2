package pl.themolka.arcade.life;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.goal.SimpleInteractableGoal;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;
import pl.themolka.arcade.util.StringId;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The objective is to kill all the enemies in the given {@link Set}.
 */
public class KillEnemies extends SimpleInteractableGoal implements StringId {
    public static final String DEFAULT_GOAL_NAME = "Kill Enemies";

    private final Set<Participator> enemies;
    private final String id;

    protected KillEnemies(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.enemies = new LinkedHashSet<>();
        for (Ref<Participator.Config<?>> enemy : config.enemies().get()) {
            this.enemies.add(library.getOrDefine(game, enemy.get()));
        }

        this.id = config.id();
    }

    @Override
    protected void complete(Participator completer) {
        if (!super.isCompleted()) {
            GoalCompleteEvent.call(this, completer);
        }
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public FinitePercentage getProgress() {
        int allies = this.getOwner().getPlayers().size();
        return Percentage.trim(PROGRESS_SCORED.getValue() / (allies + this.enemies.size()) * allies);
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

    public interface Config extends SimpleInteractableGoal.Config<KillEnemies>, Unique {
        Ref<Set<Ref<Participator.Config<?>>>> enemies();

        @Override
        default KillEnemies create(Game game, Library library) {
            return new KillEnemies(game, library, this);
        }
    }
}
