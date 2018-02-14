package pl.themolka.arcade.capture;

import pl.themolka.arcade.event.EventListenerComponent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleInteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Capturable extends SimpleInteractableGoal implements EventListenerComponent, StringId {
    protected final CaptureGame game;

    private final String id;

    public Capturable(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Capturable(CaptureGame game, Participator owner, String id) {
        super(game.getGame(), owner);
        this.game = game;

        this.id = id;
    }

    @Override
    public void complete(Participator completer) {
        CapturableCaptureEvent event = new CapturableCaptureEvent(this.game.getPlugin(), this, completer);
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
        CapturableResetEvent event = new CapturableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalResetEvent.call(this.game.getPlugin(), this);

            this.getContributions().clearContributors();
            this.setCompleted(false);
            this.setTouched(false);
            this.resetCapturable();
            return true;
        }

        return false;
    }

    public abstract void capture(Participator completer, GamePlayer player);

    public CaptureGame getCaptureGame() {
        return this.game;
    }

    public boolean registerGoal() {
        return true;
    }

    @Deprecated
    public abstract void resetCapturable();

    @Override
    public abstract String toString();
}
