package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Participator;

public class ScoreScoredEvent extends ScoreEvent implements Cancelable {
    private final boolean byLimit;
    private boolean cancel;
    private Participator completer;

    public ScoreScoredEvent(ArcadePlugin plugin, Score score, boolean byLimit, Participator completer) {
        super(plugin, score);

        this.byLimit = byLimit;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Participator getCompleter() {
        return this.completer;
    }

    public boolean hasCompleter() {
        return this.completer != null;
    }

    public boolean isByLimit() {
        return this.byLimit;
    }

    public void setCompleter(Participator completer) {
        this.completer = completer;
    }
}
