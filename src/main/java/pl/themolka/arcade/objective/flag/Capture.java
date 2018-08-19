package pl.themolka.arcade.objective.flag;

import org.bukkit.Location;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.region.tracker.PlayerTracker;
import pl.themolka.arcade.region.tracker.PlayerTrackerListener;
import pl.themolka.arcade.region.tracker.RegionTrackerFilter;

public class Capture extends pl.themolka.arcade.objective.Capture implements PlayerTrackerListener {
    private Flag flag;
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
    }

    @Override
    public void onLeave(GamePlayer player, Location leave) {
    }

    public Flag getFlag() {
        return this.flag;
    }

    public void setup(Flag flag) {
        if (this.flag != null || this.tracker != null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is already defined");
        }

        this.flag = flag;
        this.tracker = new PlayerTracker(flag.getGame(), new RegionTrackerFilter(this.getRegion()), this);
    }

    interface Config extends pl.themolka.arcade.objective.Capture.Config {
        @Override
        default Capture create(Game game, Library library) {
            return new Capture(game, library, this);
        }
    }
}
