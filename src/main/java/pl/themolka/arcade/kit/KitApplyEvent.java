package pl.themolka.arcade.kit;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.commons.event.Cancelable;

public class KitApplyEvent extends KitEvent implements Cancelable {
    private boolean cancel;
    private final GamePlayer player;

    public KitApplyEvent(ArcadePlugin plugin, Kit kit, GamePlayer player) {
        super(plugin, kit);

        this.player = player;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }
}
