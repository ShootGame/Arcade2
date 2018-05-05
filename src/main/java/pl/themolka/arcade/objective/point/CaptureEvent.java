package pl.themolka.arcade.objective.point;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;

public class CaptureEvent extends PointEvent {
    private final Capture capture;
    private final GamePlayer player;
    private final Location location;

    public CaptureEvent(Capture capture, GamePlayer player, Location location) {
        super(capture.getPoint());

        this.capture = capture;
        this.player = player;
        this.location = location;
    }

    public Capture getCapture() {
        return this.capture;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    public static class Enter extends CaptureEvent {
        public Enter(Capture capture, GamePlayer player, Location location) {
            super(capture, player, location);
        }
    }

    public static class Leave extends CaptureEvent {
        public Leave(Capture capture, GamePlayer player, Location location) {
            super(capture, player, location);
        }
    }
}
