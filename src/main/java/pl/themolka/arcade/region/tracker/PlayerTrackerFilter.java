package pl.themolka.arcade.region.tracker;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;

public interface PlayerTrackerFilter {
    boolean canTrack(GamePlayer player, Location at);

    static <T extends PlayerTrackerFilter> PlayerTrackerFilter multi(T... filters) {
        return new PlayerTrackerFilter() {
            @Override
            public boolean canTrack(GamePlayer player, Location at) {
                if (filters != null) {
                    for (T filter : filters) {
                        if (!filter.canTrack(player, at)) {
                            return false;
                        }
                    }
                }

                return true;
            }
        };
    }
}
