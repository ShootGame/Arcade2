package pl.themolka.arcade.objective.point;

import net.engio.mbassy.bus.IMessagePublication;
import org.bukkit.Location;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.tracker.PlayerTracker;
import pl.themolka.arcade.region.tracker.PlayerTrackerListener;
import pl.themolka.arcade.region.tracker.RegionTrackerFilter;

public class Capture extends pl.themolka.arcade.objective.Capture implements PlayerTrackerListener {
    private Point point;
    private PlayerTracker tracker;

    public Capture(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);
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
        @Override
        default Capture create(Game game, Library library) {
            return new Capture(game, library, this);
        }
    }
}
