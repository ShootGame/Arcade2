package pl.themolka.arcade.objective.point;

import net.engio.mbassy.bus.IMessagePublication;
import org.bukkit.Location;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.tracker.PlayerTracker;
import pl.themolka.arcade.region.tracker.PlayerTrackerListener;
import pl.themolka.arcade.region.tracker.RegionTrackerFilter;
import pl.themolka.arcade.time.Time;

public class Capture extends pl.themolka.arcade.objective.Capture implements PlayerTrackerListener {
    private final Time captureTime;
    private final Time loseTime;

    private Point point;
    private PlayerTracker tracker;

    public Capture(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.captureTime = config.captureTime();
        this.loseTime = config.loseTime();
    }

    @Override
    public PlayerTracker getTracker() {
        return this.tracker;
    }

    @Override
    public void onEnter(GamePlayer player, Location enter) {
        this.publish(new CaptureEvent.Enter(this, player, enter));
    }

    @Override
    public void onLeave(GamePlayer player, Location leave) {
        this.publish(new CaptureEvent.Leave(this, player, leave));
    }

    public Point getPoint() {
        return this.point;
    }

    public Time getCaptureTime() {
        return this.captureTime;
    }

    public Time getLoseTime() {
        return this.loseTime;
    }

    public void setup(Point point) {
        if (this.point != null || this.tracker != null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is already defined");
        }

        this.point = point;
        this.tracker = new PlayerTracker(point.getGame(), new RegionTrackerFilter(this.getRegion()), this);
    }

    private IMessagePublication publish(CaptureEvent event) {
        return this.point.getPlugin().getEventBus().publish(event);
    }

    public interface Config extends pl.themolka.arcade.objective.Capture.Config {
        Time DEFAULT_CAPTURE_TIME = Time.ofSeconds(10);
        Time DEFAULT_LOSE_TIME = Time.ofSeconds(10);

        default Time captureTime() { return DEFAULT_CAPTURE_TIME; }
        default Time loseTime() { return DEFAULT_LOSE_TIME; }

        @Override
        default Capture create(Game game, Library library) {
            return new Capture(game, library, this);
        }
    }
}
