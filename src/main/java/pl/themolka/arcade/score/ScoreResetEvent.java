package pl.themolka.arcade.score;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;

public class ScoreResetEvent extends ScoreEvent implements Cancelable {
    private boolean cancel;

    public ScoreResetEvent(ArcadePlugin plugin, Score score) {
        super(plugin, score);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
