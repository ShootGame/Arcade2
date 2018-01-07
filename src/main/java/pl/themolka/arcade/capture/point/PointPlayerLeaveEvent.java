package pl.themolka.arcade.capture.point;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;

public class PointPlayerLeaveEvent extends PointEvent {
    private final GamePlayer player;

    public PointPlayerLeaveEvent(ArcadePlugin plugin, Point point, GamePlayer player) {
        super(plugin, point);

        this.player = player;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public boolean isParticipating() {
        return this.getPlayer().isParticipating();
    }
}