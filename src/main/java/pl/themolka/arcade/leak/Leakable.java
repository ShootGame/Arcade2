package pl.themolka.arcade.leak;

import pl.themolka.arcade.event.EventListenerComponent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleInteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Leakable extends SimpleInteractableGoal implements EventListenerComponent, StringId {
    protected final LeakGame game;

    private final String id;

    public Leakable(LeakGame game, String id) {
        this(game, null, id);
    }

    public Leakable(LeakGame game, Participator owner, String id) {
        super(game.getGame(), owner);
        this.game = game;

        this.id = id;
    }

    @Override
    public void complete(Participator completer) {
        LeakableLeakEvent event = new LeakableLeakEvent(this.game.getPlugin(), this, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalCompleteEvent.call(this.game.getPlugin(), this, event.getCompleter());
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean reset() {
        LeakableResetEvent event = new LeakableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.getContributions().clearContributors();
            this.setCompleted(false);
            this.setTouched(false);
            this.resetLeakable();
            return true;
        }

        return false;
    }

    public abstract void leak(Participator completer, GamePlayer player);

    public LeakGame getLeakGame() {
        return this.game;
    }

    public boolean registerGoal() {
        return true;
    }

    @Deprecated
    public abstract void resetLeakable();

    @Override
    public abstract String toString();
}
