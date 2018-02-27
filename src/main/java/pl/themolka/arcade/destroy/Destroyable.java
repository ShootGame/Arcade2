package pl.themolka.arcade.destroy;

import pl.themolka.arcade.event.EventListenerComponent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleInteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Destroyable extends SimpleInteractableGoal implements EventListenerComponent, StringId {
    protected final DestroyGame game;

    private final String id;

    public Destroyable(DestroyGame game, String id) {
        this(game, null, id);
    }

    public Destroyable(DestroyGame game, Participator owner, String id) {
        super(game.getGame(), owner);
        this.game = game;

        this.id = id;
    }

    @Override
    public void complete(Participator completer) {
        DestroyableDestroyEvent event = new DestroyableDestroyEvent(this.game.getPlugin(), this, completer);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalCompleteEvent.call(this.game.getPlugin(), this, completer);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean reset() {
        DestroyableResetEvent event = new DestroyableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.getContributions().clearContributors();
            this.setCompleted(false);
            this.setTouched(false);
            this.resetDestroyable();
            return true;
        }

        return false;
    }

    public abstract void destroy(Participator completer, GamePlayer player);

    public DestroyGame getDestroyGame() {
        return this.game;
    }

    public boolean registerGoal() {
        return true;
    }

    @Deprecated
    public abstract void resetDestroyable();

    @Override
    public abstract String toString();}
